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
  self.skipWaiting();
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(FILES)));
});

self.addEventListener('activate', e => {
  e.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.map(k => (k !== CACHE ? caches.delete(k) : Promise.resolve())));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', e => {
  const url = new URL(e.request.url);
  const isNavigate = e.request.mode === 'navigate';
  const isIndex = url.origin === self.location.origin && (url.pathname === '/' || url.pathname === '/index.html');

  // Always try network first for app shell HTML so UI updates are not stuck behind SW cache
  if (isNavigate || isIndex) {
    e.respondWith((async () => {
      try {
        const fresh = await fetch(e.request);
        const cache = await caches.open(CACHE);
        cache.put('/index.html', fresh.clone());
        cache.put('/', fresh.clone());
        return fresh;
      } catch {
        const cached = await caches.match(e.request);
        return cached || caches.match('/index.html');
      }
    })());
    return;
  }

  // Cache-first for static assets
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
