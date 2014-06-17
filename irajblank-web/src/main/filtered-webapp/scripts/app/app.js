'use strict';

angular.module('blank-iraj', [
		'ngRoute',
		'raajAuthenticationService',
		'raajSecurityInterceptor',
		'raaj-message-interceptor',
		'raaj-message-service',
		'iraj-breadcrumbs-service',
		'raaj-progress-interceptor',
		'raaj-table-service'
	])
	.config(function (raajProgressInterceptorProvider) {
		raajProgressInterceptorProvider.setDefaultMessage('Chargement en cours...');
	})
	.config(function (raajMessageServiceProvider) {
		raajMessageServiceProvider.setBootstrapVersion('3.x');
	})
;