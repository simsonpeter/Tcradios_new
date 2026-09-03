const CACHE = 'tcr-v7';
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
const MEDIA_TAG = 'tcradios-now-playing';

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(FILES)));
  self.skipWaiting();
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys().then(keys => Promise.all(
      keys.filter(key => key !== CACHE).map(key => caches.delete(key))
    ))
  );
  return self.clients.claim();
});

self.addEventListener('fetch', e => {
  const url = new URL(e.request.url);
  if (e.request.url.endsWith('.html') || url.pathname === '/') {
    e.respondWith(
      fetch(e.request, { cache: 'no-store' })
        .then(res => {
          const resClone = res.clone();
          caches.open(CACHE).then(cache => cache.put(e.request, resClone));
          return res;
        })
        .catch(() => caches.match(e.request))
    );
  } else {
    e.respondWith(caches.match(e.request).then(res => res || fetch(e.request)));
  }
});

function broadcastToClients(data) {
  return self.clients.matchAll({ type: 'window', includeUncontrolled: true }).then(clients => {
    clients.forEach(client => client.postMessage(data));
  });
}

self.addEventListener('message', event => {
  const data = event.data || {};
  if (data.type === 'ANDROID_AUTO_COMMAND') {
    broadcastToClients(data);
    return;
  }
  if (data.type === 'MEDIA_NOTIFICATION') {
    event.waitUntil(showNowPlayingNotification(data));
    return;
  }
  if (data.type === 'MEDIA_NOTIFICATION_CLEAR') {
    event.waitUntil(self.registration.getNotifications({ tag: MEDIA_TAG })
      .then(list => list.forEach(n => n.close())));
  }
});

function resolveMediaUrl(path) {
  if (!path) return new URL('/icons/icon-512x512.png', self.location.origin).href;
  try {
    return new URL(path, self.location.origin).href;
  } catch (e) {
    return new URL('/icons/icon-512x512.png', self.location.origin).href;
  }
}

async function showNowPlayingNotification(data) {
  const playing = !!data.playing;
  const title = data.title || data.name || 'TC RADIOS';
  const subtitle = data.subtitle || data.artist || (playing ? 'Live • TC RADIOS' : 'Paused • TC RADIOS');
  const album = data.album || data.name || 'TC RADIOS';
  const icon = resolveMediaUrl(data.icon || data.artwork);
  const artwork = resolveMediaUrl(data.artwork || data.icon);

  if (!playing) {
    const existing = await self.registration.getNotifications({ tag: MEDIA_TAG });
    existing.forEach(n => n.close());
    return;
  }

  try {
    await self.registration.showNotification(title, {
      body: subtitle,
      icon,
      image: artwork,
      badge: resolveMediaUrl('/icons/icon-192x192.png'),
      tag: MEDIA_TAG,
      renotify: true,
      silent: true,
      requireInteraction: true,
      actions: [
        { action: 'prev', title: '⏮ Previous' },
        { action: playing ? 'pause' : 'play', title: playing ? '⏸ Pause' : '▶ Play' },
        { action: 'next', title: 'Next ⏭' }
      ],
      data: {
        name: data.name || title,
        album,
        artist: data.artist || subtitle,
        playing
      }
    });
  } catch (e) {
    // Notification permission may be missing.
  }
}

self.addEventListener('notificationclick', event => {
  const action = event.action;
  const isTransport = action === 'play' || action === 'pause' || action === 'next' || action === 'prev';
  if (!isTransport) {
    event.notification.close();
  }
  event.waitUntil((async () => {
    const command = action === 'play' ? 'PLAY'
      : action === 'pause' ? 'PAUSE'
      : action === 'next' ? 'NEXT'
      : action === 'prev' ? 'PREVIOUS'
      : null;
    const clientsList = await self.clients.matchAll({ type: 'window', includeUncontrolled: true });
    if (command) {
      if (clientsList.length) {
        clientsList.forEach(client => {
          client.postMessage({ type: 'ANDROID_AUTO_COMMAND', command: { action: command } });
        });
        if (clientsList[0].focus) await clientsList[0].focus();
        return;
      }
      const url = new URL('/', self.location.origin);
      url.searchParams.set('action', action === 'play' ? 'play' : action === 'pause' ? 'pause' : action);
      await self.clients.openWindow(url.toString());
      return;
    }
    if (clientsList.length) {
      if (clientsList[0].focus) await clientsList[0].focus();
      return;
    }
    await self.clients.openWindow('/');
  })());
});
