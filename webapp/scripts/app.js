'use strict';

/**
 * @ngdoc overview
 * @name panelApp
 * @description
 * # panelApp
 *
 * Main module of the application.
 */
angular
  .module('panelApp', [
    'ngAnimate',
    'ngCookies',
    'ngMessages'
  ])
  .config(function($httpProvider) {
    // Configure all post request as urlencoded
    $httpProvider.defaults.headers.post = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    $httpProvider.defaults.headers.put = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    $httpProvider.defaults.transformRequest = function(obj) {
      var str = [];
      for (var p in obj) {
        str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
      }
      return str.join('&');
    };
  });
