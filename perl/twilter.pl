#!/opt/portage/usr/bin/perl -w -p

use POSIX qw/strftime/;
use Date::Parse qw/strptime/; 

if (/^\s*$/ || /^{"delete":/) { $_ = ""; next }

#print "line: $_\n";

sub iso_date;
sub user_id;

s/("user":\{(?:\"[^"]+\":(?:[^"{}:,]+|"(?:[^"]|\\")*"),?)+\},?)/user_id($1)/eg;

# how can we precompile the regexp?
$created_at = "\"created_at\":\""; #"
$created_at_r = $created_at."([^\"]+)\""; 

s/$created_at_r/iso_date("$1")/eg; 

sub iso_date {
	$res = "";
	if (@_ > 0) {
	$x = shift @_;
		@iso = strptime($x);
		eval {
			$res = strftime('%Y-%m-%d %T',@iso);
		};
		if ($@) {
			$res = "0000-00-00 00:00:00";
		}
	}
	$created_at.$res.'"'
}

sub user_id {
	$user = shift @_;
	$user =~ /.*"id":(\d+)/;
	$id = $1;
	$user =~ /.*"screen_name":\"((\\"|[^"])*)\"/;
	$screen_name = $1;
	$user =~ /.*(,)$/;
	$opt_comma = $1;
	"\"user_id\":$id,\"screen_name\":\"$screen_name\"$opt_comma"
}