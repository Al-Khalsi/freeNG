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
        bgWhite: "var(--bg-fff)",
        bgGray: "var(--bg-gray)",
        bgBlack: "var(--bg-black)",
        bgTurquoise: "var(--bg-turquoise)",
        bgDarkBlue: "var(--bg-darkBlue)",
        bgNavyBlue: "var(--bg-navyBlue)",
        bgLightBlue: "var(--bg-lightBlue)",
        bgDarkGray: "var(--bg-darkGray)",
        bgDarkGray2: "var(--bg-darkGray2)",
        clWhite: "var(--cl-fff)",
        clGray: "var(--cl-gray)",
        clBlack: "var(--cl-black)",
        clTurquoise: "var(--cl-turquoise)",
        clDarkBlue: "var(--cl-darkBlue)",
        clNavyBlue: "var(--cl-navyBlue)",
        clLightBlue: "var(--cl-lightBlue)",
        clDarkGray: "var(--cl-darkGray)",
        clDarkGray2: "var(--cl-darkGray2)",
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
  },
  plugins: [
    require('tailwind-scrollbar'),
  ],
};
