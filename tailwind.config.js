/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      width: {
        "custom-212": "53rem", // اضافه کردن عرض کاستوم با نام w-212
      },
      height: {
        "custom-136": "34rem",
      },
      colors: {
        bgWhite: "var(--bg-white)",
        bgGray: "var(--bg-gray)",
        bgBlack: "var(--bg-black)",
        bgDarkPurple: "var(--bg-darkPurple)",
        bgPurple: "var(--bg-purple)",
        bgLightPurple: "var(--bg-lightPurple)",
        bgDarkBlue: "var(--bg-darkBlue)",
        bgNavyBlue: "var(--bg-navyBlue)",
        bgLightBlue: "var(--bg-lightBlue)",
        bgDarkGray: "var(--bg-darkGray)",
        bgDarkGray2: "var(--bg-darkGray2)",
        clWhite: "var(--cl-white)",
        clGray: "var(--cl-gray)",
        clBlack: "var(--cl-black)",
        clDarkPurple: "var(--cl-darkPurple)",
        clPurple: "var(--cl-purple)",
        clLightPurple: "var(--cl-lightPurple)",
        clDarkBlue: "var(--cl-darkBlue)",
        clNavyBlue: "var(--cl-navyBlue)",
        clLightBlue: "var(--cl-lightBlue)",
        clDarkGray: "var(--cl-darkGray)",
        clDarkGray2: "var(--cl-darkGray2)",
      },
    },
    screens: {
      'sm': '426px',
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
  },
  plugins: [
    require('tailwind-scrollbar'),
  ],
};
