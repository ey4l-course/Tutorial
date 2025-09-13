export const validateUsername = (val) =>
  /^(?!.*\s)[a-zA-Z0-9._\-$^~]{1,20}$/.test(val);

export const checkPasswordRules = (val) => ({
  capital: /[A-Z]/.test(val),
  small: /[a-z]/.test(val),
  digit: /\d/.test(val),
  symbol: /[-!@#$%^&*()_./]/.test(val),
  length: val.length >= 8
});

export const validateName = (val) =>
  /^(?=.{2,20}$)[a-zA-Z]{2,}(?: [a-zA-Z]{2,})?$/.test(val);

export const validateMobile = (val) =>
  /^[0-9]{9,10}$/.test(val);

export const validateEmail = (val) =>
  /^[a-zA-Z][a-zA-Z0-9-_~+.]{0,44}[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9-.]{0,13}[a-zA-Z0-9](\.[a-zA-Z]{2,3}){1,2}$/.test(val);