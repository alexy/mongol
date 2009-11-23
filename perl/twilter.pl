#!/opt/portage/usr/bin/perl -w -p

use POSIX qw/strftime/;
use Date::Parse qw/strptime/; 

if (/^{"delete":/) { $_ = ""; next }

#print "line: $_\n";

s/"user":\{(?:\"[^"]+\":(?:[^"{}:]+|"[^"]+"),?)+\},?//g;

$created_at = "\"created_at\":\""; #"
$created_at_r = $created_at."([^\"]+)\""; 
#print "datetime pattern => $created_at\n";

$_ =~ s/$created_at_r/qq{iso_date("$1")}/gee; 

sub iso_date {
	$x = shift @_;
	@iso = strptime($x);
	$res = strftime('%Y-%m-%d %T',@iso);
	$created_at.$res.'"'
}
