importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "AIzaSyB7katPoiGleR-N9o1Vm8AVE74sxknT2bM",
  authDomain: "quan-ly-tai-san-79839.firebaseapp.com",
  projectId: "quan-ly-tai-san-79839",
  storageBucket: "quan-ly-tai-san-79839.firebasestorage.app",
  messagingSenderId: "635558931195",
  appId: "1:635558931195:web:8dadb8ebb3c3e37e841e5e",
  measurementId: "G-7RJS9MDB9M"
});

const messaging = firebase.messaging();
