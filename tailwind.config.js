/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
      },
    },
    screens: {
      'sm': '425px',
      // => @media (min-width: 640px) { ... }

      'md': '426px',
      // => @media (min-width: 768px) { ... }

      'lg': '769px',
      // => @media (min-width: 1024px) { ... }

      'xl': '1025px',
      // => @media (min-width: 1280px) { ... }
    },
    container: {
      "center": true,
      "padding": "3rem",
      "margin": "3rem"
    }
  },
  plugins: [],
};
