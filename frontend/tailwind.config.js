/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#FFFFFF",
        "secondary-bg": "#FAFAFC",
        "card-bg": "#FFFFFF",
        primary: {
          DEFAULT: "#9B87F5",
          hover: "#8B75F3",
          light: "#F1EDFF",
          dark: "#7B64EB",
        },
        lavender: {
          50: "#FAF9FF",
          100: "#F1EDFF",
          200: "#E3DCFE",
          300: "#C9BEFD",
          400: "#B09FFB",
          500: "#9B87F5",
          600: "#8066EE",
          700: "#6549E0",
          800: "#4D34B8",
          900: "#36228A",
        },
        sage: {
          50: "#F7FAF8",
          100: "#EFF7F1",
          200: "#DCEEE1",
          300: "#C3E1CC",
          400: "#A8C9B5",
          500: "#8BB59A",
          600: "#6FA080",
          700: "#558766",
        },
        finova: {
          text: "#292735",
          muted: "#858392",
          border: "#EEEEF2",
          card: "#FFFFFF",
          success: "#65A982",
          warning: "#E8B96B",
          danger: "#DF858B",
        }
      },
      borderRadius: {
        'card': '18px',
        'card-lg': '22px',
        'card-sm': '14px',
      },
      boxShadow: {
        'subtle': '0 2px 10px rgba(41, 39, 53, 0.04)',
        'subtle-hover': '0 8px 24px rgba(155, 135, 245, 0.08)',
        'card': '0 1px 3px rgba(0, 0, 0, 0.02), 0 4px 16px rgba(41, 39, 53, 0.03)',
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
