const CACHE = 'tcr-v3';
const FILES = [
  '/',
  '/index.html',
  '/manifest.json',
  '/widget-template.html',
  '/icons/icon-192x192.png',
  '/icons/icon-512x512.png',
  '/icons/favicon.ico',
  '/icons/favicon-16x16.png',
  '/icons/favicon-32x32.png',
  '/icons/apple-touch-icon.png',
  '/icons/default-artwork.jpg'
];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(FILES)));
  self.skipWaiting(); // Activate immediately
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.map(key => {
          if (key !== CACHE) {
            return caches.delete(key);
          }
        })
      );
    })
  );
  return self.clients.claim(); // Take control of all pages immediately
});

self.addEventListener('fetch', e => {
  // For HTML files, try network first, then cache
  if (e.request.url.endsWith('.html') || e.request.url === new URL(e.request.url).origin + '/') {
    e.respondWith(
      fetch(e.request)
        .then(res => {
          // Clone the response
          const resClone = res.clone();
          // Update cache
          caches.open(CACHE).then(cache => {
            cache.put(e.request, resClone);
          });
          return res;
        })
        .catch(() => caches.match(e.request))
    );
  } else {
    // For other files, use cache first
    e.respondWith(
      caches.match(e.request).then(res => res || fetch(e.request))
    );
  }
});

// Android Auto support
self.addEventListener('message', event => {
  if (event.data.type === 'ANDROID_AUTO_COMMAND') {
    // Forward Android Auto commands to main app
    self.clients.matchAll().then(clients => {
      clients.forEach(client => {
        client.postMessage(event.data);
      });
    });
  }
});

// Handle Android Auto media session updates
self.addEventListener('notificationclick', event => {
  event.notification.close();
  
  // Handle Android Auto notification clicks
  if (event.action === 'play') {
    self.clients.matchAll().then(clients => {
      clients.forEach(client => {
        client.postMessage({ type: 'ANDROID_AUTO_COMMAND', command: { action: 'PLAY' } });
      });
    });
  } else if (event.action === 'pause') {
    self.clients.matchAll().then(clients => {
      clients.forEach(client => {
        client.postMessage({ type: 'ANDROID_AUTO_COMMAND', command: { action: 'PAUSE' } });
      });
    });
  }
});
