import { Html, Head, Main, NextScript } from "next/document";

export default function Document() {
  return (
    <Html lang="en">
      <Head>
        <link rel="icon" href="/favicon.ico" />
        {/* Favicon for standard browsers */}
        <link rel="icon" type="image/png" href="/img/LOGO.png" sizes="16x16" />
        <link rel="icon" type="image/png" href="/img/LOGO-icon-32x32.png" sizes="32x32" />
        <link rel="icon" type="image/png" href="/img/LOGO-icon-48x48.png" sizes="48x48" />
        <link rel="icon" type="image/png" href="/img/LOGO-icon-192x192.png" sizes="192x192" />
        <link rel="icon" type="image/png" href="/img/LOGO-icon-512x512.png" sizes="512x512" />

        {/* Apple Touch Icon for iOS devices */}
        <link rel="apple-touch-icon" href="/img/LOGO-icon-180x180.png" sizes="180x180" />
        <link rel="apple-touch-icon" href="/img/LOGO-icon-152x152.png" sizes="152x152" />
        <link rel="apple-touch-icon" href="/img/LOGO-icon-120x120.png" sizes="120x120" />

        {/* Android Chrome Icon */}
        <link rel="icon" type="image/png" href="/img/LOGO-icon-192x192.png" sizes="192x192" />

        {/* Microsoft Tiles for Windows */}
        <meta name="msapplication-TileColor" content="#ffffff" />
        <meta name="msapplication-TileImage" content="/img/LOGO-icon-270x270.png" />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}
