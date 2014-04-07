'use strict';

angular.module('blank-iraj', [
		'ngRoute',
		'raaj-authentication-service',
		'raaj-security-interceptor',
		'raaj-message-interceptor',
		'raaj-message-service',
		'iraj-breadcrumbs-service',
		'raaj-progress-interceptor',
		'raaj-table-service'
	])
	.config(function (irajProgressInterceptorProvider) {
		irajProgressInterceptorProvider.setDefaultMessage('Chargement en cours...');
	})
	.config(function (irajMessageServiceProvider) {
		irajMessageServiceProvider.setBootstrapVersion('3.x');
	})
;