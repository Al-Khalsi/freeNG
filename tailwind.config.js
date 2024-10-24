/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      width: {
        "custom-212": "53rem", // اضافه کردن عرض کاستوم با نام w-212
      },
      height: {
        "custom-136": "34rem",
      },
      colors: {
        darkBlue: "var(--bg-darkBlue)",
        lightBlue: "var(--cl-lightBlue)",
        clGray: "var(--cl-gray)",
        lightGray: "var(--bg-lightGray)",
        lightBlue2: "var(--bg-lightBlue2)",
        white: "var(--bg-white)",
        darkGray: "var(--cl-darkGray)",
        inputwhite: "var(--bg-input)"
      },
    },
    screens: {
      'sm': '425px',
      // => @media (min-width: 640px) { ... }

      'md': '769px',
      // => @media (min-width: 768px) { ... }

      'lg': '1024px',
      // => @media (min-width: 1024px) { ... }

      'xl': '1280px',
      // => @media (min-width: 1280px) { ... }

      '2xl': '1536px',
      // => @media (min-width: 1536px) { ... }
    },
    container: {
      "center": true,
      "padding": "3rem",
      "margin": "3rem"
    }
  },
  plugins: [
    require('tailwind-scrollbar'),
  ],
};
