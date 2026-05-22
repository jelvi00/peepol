import '@testing-library/jest-dom';
import 'jest-canvas-mock';

// Mock TextEncoder/TextDecoder if needed (often needed for axios/paseto in jsdom)
if (typeof global.TextEncoder === 'undefined') {
  const { TextEncoder, TextDecoder } = require('util');
  global.TextEncoder = TextEncoder;
  global.TextDecoder = TextDecoder;
}
