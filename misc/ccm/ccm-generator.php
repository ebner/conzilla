<?php
	header('Content-type: text/conzilla-context-map');
	header('Content-Disposition: inline; filename="context-map.ccm"');
	header('Content-Description: A Conzilla Context-Map description file');
	echo "[Context-Map]\r\n";
	echo "uri = ".urldecode($_GET["uri"]);
?>
