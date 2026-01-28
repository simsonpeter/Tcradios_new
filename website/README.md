# TC RADIOS Website

A modern, responsive website showcasing the TC RADIOS Christian radio platform on Android and Web (PWA).

## Features

- **Responsive Design**: Works perfectly on desktop, tablet, and mobile devices
- **Modern UI**: Clean, professional design with smooth animations
- **Platform Showcase**: Focused on Android app and Web app (PWA, installable on Android and iPhone)
- **Download Integration**: Direct links to download apps for each platform
- **SEO Optimized**: Meta tags, Open Graph, and Twitter Card support
- **Performance Optimized**: Lazy loading, optimized images, and efficient CSS/JS

## Structure

```
website/
├── index.html              # Main landing page
├── css/
│   └── style.css          # Main stylesheet
├── js/
│   └── script.js          # Main JavaScript
├── platforms/
│   ├── android.html       # Android app showcase
│   └── css/
│       └── platform.css   # Platform-specific styles
└── README.md             # This file
```

## Pages

### Main Landing Page (`index.html`)
- Hero section with app overview
- Features showcase
- Platform cards with links to detailed pages
- Download section with direct links
- About section
- Footer with navigation

### Platform Pages (`platforms/`)
- **Android**: Detailed Android app information, installation guide, screenshots

## Technologies Used

- **HTML5**: Semantic markup with accessibility features
- **CSS3**: Modern CSS with Grid, Flexbox, and animations
- **JavaScript**: Vanilla JS for interactions and animations
- **Font Awesome**: Icons for UI elements
- **Google Fonts**: Inter font family for typography

## Features Implemented

### Navigation
- Fixed navbar with smooth scrolling
- Mobile-responsive hamburger menu
- Active link highlighting
- Smooth scroll to sections

### Animations
- Intersection Observer for scroll animations
- Counter animations for statistics
- Hover effects on cards and buttons
- Ripple effects on button clicks
- Floating cards animation in hero section

### Interactive Elements
- Download tracking and feedback
- Language switcher simulation
- Social media link handlers
- Error handling for failed downloads
- Keyboard navigation support

### Performance
- Lazy loading for images
- Optimized CSS and JavaScript
- Efficient animations using CSS transforms
- Minimal external dependencies

## Browser Support

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+
- Mobile browsers (iOS Safari, Chrome Mobile)

## Installation

1. Clone or download the website files
2. Ensure all file paths are correct relative to your web server
3. Update image paths and download links as needed
4. Deploy to your web server

## Customization

### Colors
Update CSS custom properties in `style.css`:
```css
:root {
    --primary-color: #f97316;
    --secondary-color: #1f2937;
    /* ... other variables */
}
```

### Content
- Update text content in HTML files
- Replace placeholder images with actual screenshots
- Update download links to point to actual files
- Modify platform-specific information

### Styling
- Customize animations in CSS
- Add new sections or modify existing ones
- Update responsive breakpoints as needed

## SEO Features

- Meta descriptions and keywords
- Open Graph tags for social sharing
- Twitter Card support
- Semantic HTML structure
- Alt text for images
- Proper heading hierarchy

## Accessibility

- Semantic HTML elements
- Alt text for images
- Keyboard navigation support
- High contrast colors
- Focus indicators
- Screen reader friendly

## Performance

- Optimized images
- Minified CSS and JavaScript (when deployed)
- Efficient animations
- Lazy loading
- Minimal external requests

## License

This website is part of the TC RADIOS project. All rights reserved.

## Support

For questions or issues with the website, please contact the TC RADIOS team.
