import React from "react";
import CookieConsent from "react-cookie-consent";

const CookieBanner = () => {
  return (
    <CookieConsent location="bottom">This website uses cookies</CookieConsent>
  );
};

export default CookieBanner;
