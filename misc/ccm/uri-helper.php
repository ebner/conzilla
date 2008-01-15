<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
	<script language="javascript">
	<!--
	function urlencode(plainurl) {
		var SAFECHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'()";
		var HEX = "0123456789ABCDEF";
		var encoded = "";
		
		for (var i = 0; i < plainurl.length; i++ ) {
			var ch = plainurl.charAt(i);
			if (ch == " ") {
				encoded += "+"; // x-www-urlencoded, rather than %20
			} else if (SAFECHARS.indexOf(ch) != -1) {
				encoded += ch;
			} else {
				var charCode = ch.charCodeAt(0);
				if (charCode > 255) {
					encoded += "+";
				} else {
					encoded += "%";
					encoded += HEX.charAt((charCode >> 4) & 0xF);
					encoded += HEX.charAt(charCode & 0xF);
				}
			}
		}
		return encoded;
	};

	function setDetailsVisible(visible) { // 1 visible, 0 hidden
		if (document.layers) {
			document.layers["details"].visibility = visible ? "show" : "hide";
			document.layers["details"].display = visible ? "block" : "none";
		} else if (document.getElementById) {
			var obj = document.getElementById("details");
			obj.style.visibility = visible ? "visible" : "collapse";
			obj.style.display = visible ? "block" : "none";
		} else if (document.all) {
			document.all["details"].style.visibility = visible ? "visible" : "hidden";
			document.all["details"].style.display = visible ? "block" : "none";
		}
	}

	// preloading image(s)
	if (document.images) {
		img1 = new Image(337,96); 
		img1.src = "/ccm/button96-pressed.png";
	}
	-->
	</script>

	<title>You are trying to load a Conzilla Context-Map</title>
	<link rel="shortcut icon" type="image/ico" href="/favicon.ico" />
</head>

<body style="width: 600px; margin: 5%; font-family: sans-serif;">

<!--<h2 align="right">conzilla.org</h2>-->

<p align="center">

<?php
//document.write("\"><img src=\"/webstart/img/conzilla32.png\" align=\"absmiddle\" height=\"32\" width=\"32\" alt=\"Conzilla Icon\" style=\"border: 0px;\"> Download a metafile for this Conzilla Context-map</a>");
?>

<script language="javascript">
<!--
	document.write("<img src=\"/ccm/button96.png\" onmousedown=\"this.src='/ccm/button96-pressed.png';\" onmouseup=\"this.src='/ccm/button96.png'\" style=\"border: 0px;\" onclick=\"window.location.href='/ccm/ccm-generator.php?uri=" + urlencode(window.location.href) + "'\"/>");
-->
</script>

</p>

<br/>

<hr style="border: 0px; background-color: #000000; height: 1px;"/>

<p align="right" style="font-size: 8pt;"><a href="javascript:setDetailsVisible(1);" style="text-decoration:none;">I don't know what this is about, show me more information!</a></p>

<div id="details" style="visibility: collapse; display: none;">

<h3>Why do I see this page?</h3>

<p align="justify">You see this page because you tried to load a <a href="http://www.conzilla.org">Conzilla</a> Context-map with an ordinary web browser. Conzilla uses it's own file format for storing the information describing a Context-map, so it is not possible to load a map in this environment (yet).</p>

<h3>So, how do I load this Context-map then?</h3>

<h4>Install Conzilla</h4>

<p align="justify">If you haven't done this yet: install and lauch Conzilla by clicking on the following button. This will run Conzilla using <a href="http://java.sun.com/products/javawebstart/">Java Web Start</a>, which is the easiest way of installing and running Conzilla. You will need a <a href="http://www.java.com/getjava/">Java Runtime Environment</a> in version 1.5 or newer.</p>

<p align="center"><a href="http://www.conzilla.org/webstart/conzilla.jnlp"><img src="http://www.conzilla.org/pics/jws-launch-button.png" style="border: 0px" alt="Launch"/></a></p>

<h4>Alternative 1: By downloading and opening a file</h4>

<p align="justify">You can use this alternative if you have properly installed Conzilla 2.2 or newer and if Java was able to associate the correct MIME-type respectively file extension.</p>

<p>Just click on the following link and open the file with Conzilla:</p>

<p align="center">
<script language="javascript">
<!--
	document.write("<a style=\"text-decoration: none;\" href=\"/ccm/ccm-generator.php?uri=");
	document.write(urlencode(window.location.href));
	document.write("\"><img src=\"/ccm/ccm32.png\" align=\"absmiddle\" height=\"32\" width=\"32\" alt=\"Conzilla Icon\" style=\"border: 0px;\">&nbsp;&nbsp;Conzilla Context-map description file</a>");
-->
</script>
</p>

<h4>Alternative 2: By manually launching Conzilla and pasting this URL</h4>

<p align="justify">In case the first alternative does not work for you, you can manually open the Context-map by copying and pasting the URI into Conzilla's address bar.</p>

<p align="center"><img src="/wiki/Doc/Exchange?action=download&upname=urifield.png"></p>

<p align="justify">To open a Context-map, paste its URI (which is the same as this documents URL in your browser's address bar) into the field labeled "Context-Map URI" (see screenshot) and press enter. Alternatively you can press the key combination "Ctrl + O" (inside Conzilla) which opens a small window where you can paste the URI into.</p>

<h3>I tried everything, but I just can't open the Context-map!</h3>

<p align="justify">If, for some reason, you are not able to open the Context-map, please do not hesitate contacting the Conzilla team using the <a href="https://lists.sourceforge.net/lists/listinfo/conzilla-users">Conzilla Users mailing list</a>.</p>

<hr style="border: 0px; background-color: #000000; height: 1px;"/>

<?php
$filemod = filemtime('uri-helper.php');
$filemodtime = date("Y-m-d H:i:s T", $filemod);
echo "<p align=\"right\" style=\"font-size: 8pt;\">Last modified: ".$filemodtime."</p>";
?>

</div>

</body>
</html>
