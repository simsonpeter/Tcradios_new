const CACHE = 'tcr-v2';
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
});

self.addEventListener('fetch', e => {
  e.respondWith(
    caches.match(e.request).then(res => res || fetch(e.request))
  );
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
