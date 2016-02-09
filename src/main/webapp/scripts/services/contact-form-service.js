'use strict';

/**
 * @ngdoc service
 * @name panelApp.contactFormService
 * @description
 * # contactFormService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('contactFormService', function($http) {
    return function(contactFormData) {
      return $http.post('/panel/api/otrs/sendIssue', contactFormData);
    };
  });
