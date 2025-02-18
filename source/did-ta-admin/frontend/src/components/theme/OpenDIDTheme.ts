import { createGlobalStyle, DefaultTheme } from 'styled-components';
import NotoSansKR from '../../assets/fonts/NotoSansKR-Regular.otf';

export const theme: DefaultTheme = {
  white: '#f7f7f7',
  black: '#000',
  blue: '#2148c0',
  grayBackground: '#DFDFDF',
  gray50: '#E9E9E9',
  gray100: '#BCBCBC',
  gray200: '#9B9B9B',
  gray300: '#6E6E6E',
  gray400: '#515151',
  gray600: '#232323',
  info: '#5186DB',
  success: '#03CF5D',
  warning: '#FCC900',
  red: 'red',
  error: '#B00020',
  sidebar: '#3c4b64',
  header: 'white',
  pointColor: '#FF6700',
  raon: {
    light: '#fff0e6',
    lightHover: '#ffe8d9',
    lightActive: '#ffd0b0',
    normal: '#ff6700',
    normalHover: '#e65d00',
    normalActive: '#cc5200',
    dark: '#bf4d00',
    darkHover: '#993e00',
    darkActive: '#732e00',
    darker: '#592400',
  },
  orange: {
    light: '#fff6e6',
    lightHover: '#fff2d9',
    lightActive: '#ffe4b0',
    normal: '#ffa800',
    normalHover: '#e69700',
    normalActive: '#cc8600',
    dark: '#bf7e00',
    darkHover: '#996500',
    darkActive: '#734c00',
    darker: '#593b00',
  },
  grey: {
    light: '#ebebeb',
    lightHover: '#e0e0e0',
    lightActive: '#c0c0c0',
    normal: '#333333',
    normalHover: '#2e2e2e',
    normalActive: '#292929',
    dark: '#262626',
    darkHover: '#1f1f1f',
    darkActive: '#171717',
    darker: '#121212',
  },
};

export const GlobalStyle = createGlobalStyle`
  @font-face {
    font-family: 'NotoSansKR';
    src: local('NotoSansKR'), url(${NotoSansKR}) format('truetype');
    font-style: normal;
  }

  *, *::before, *::after {
    box-sizing: border-box;
  }
  html, body {
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
    font-family: 'NotoSansKR', sans-serif;
    background-color: rgba(247, 247, 247, 0.80);
  }
  #root {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
  }
  form {
    width: 100%;
  }

  html, body, div, span, applet, object, iframe,
  h1, h2, h3, h4, h5, h6, p, blockquote, pre,
  a, abbr, acronym, address, big, cite, code,
  del, dfn, em, img, ins, kbd, q, s, samp,
  small, strike, strong, sub, sup, tt, var,
  b, u, i, center,
  dl, dt, dd, ol, ul, li,
  fieldset, form, label, legend,
  table, caption, tbody, tfoot, thead, tr, th, td,
  article, aside, canvas, details, embed, 
  figure, figcaption, footer, header, hgroup, 
  menu, nav, output, ruby, section, summary,
  time, mark, audio, video, textarea, button, input {
    margin: 0;
    padding: 0;
    border: 0;
    font-size: 100%;
    font: inherit;
    vertical-align: baseline;
    font-family: 'NotoSansKR', sans-serif !important;
  }

  article, aside, details, figcaption, figure, 
  footer, header, hgroup, menu, nav, section {
    display: block;
  }

  body {
    line-height: 1;
    width: 100%;
    height: 100%;
  }

  ol, ul {
    list-style: none;
  }

  blockquote, q {
    quotes: none;
  }

  blockquote:before, blockquote:after,
  q:before, q:after {
    content: '';
    content: none;
  }

  table {
    border-collapse: collapse;
    border-spacing: 0;
  }

  input:-webkit-autofill,
  input:-webkit-autofill:hover,
  input:-webkit-autofill:focus,
  input:-webkit-autofill:active {
    -webkit-text-fill-color: #000;
    -webkit-box-shadow: 0 0 0px 1000px #fff inset;
    box-shadow: 0 0 0px 1000px #fff inset;
    transition: background-color 5000s ease-in-out 0s;
  }

  input:autofill,
  input:autofill:hover,
  input:autofill:focus,
  input:autofill:active {
    -webkit-text-fill-color: #000;
    -webkit-box-shadow: 0 0 0px 1000px #fff inset;
    box-shadow: 0 0 0px 1000px #fff inset;
    transition: background-color 5000s ease-in-out 0s;
  }

  .MuiDrawer-paper{
    ::-webkit-scrollbar {
      width: 10px !important;
      background-color: white;
    }

    ::-webkit-scrollbar-track {
      background-color: #333333;
    }

    ::-webkit-scrollbar-thumb {
      width: 10px !important;
      background-color: #1b1a1a;
      border-radius: 10px;
    }
  }
`;
