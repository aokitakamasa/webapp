'use strict';

/**
 * @ngdoc service
 * @name panelApp.newsService
 * @description
 * # newsService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('newsService', function($http) {
    return $http.get('/api/news');
  });
