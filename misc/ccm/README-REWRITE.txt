<IfModule mod_rewrite.c>
	RewriteEngine On
	RewriteRule ^/people/(.*)       /ccm/uri-helper.php?uri=http://%{HTTP_HOST}%{REQUEST_URI} [L]
	RewriteRule ^/users/(.*)        /ccm/uri-helper.php?uri=http://%{HTTP_HOST}%{REQUEST_URI} [L]
	RewriteRule ^/projects/(.*)     /ccm/uri-helper.php?uri=http://%{HTTP_HOST}%{REQUEST_URI} [L]
	RewriteRule ^/model/(.*)        /ccm/uri-helper.php?uri=http://%{HTTP_HOST}%{REQUEST_URI} [L]
</IfModule>
