#!/usr/bin/perl -w -p
BEGIN { $ENV{PERL_JSON_BACKEND} = 2 } # with JSON::XS

use POSIX qw/strftime/;
use Date::Parse qw/strptime/; 
use JSON;
use Encode qw/decode/;

if (/^\s*$/ || /^{"delete":/) { $_ = ""; next }

my $json = new JSON::XS;

# ->utf8 was causing a 0xffff being rejected as an illegal unicode character
# apparently utf8 just declares validation, let's try without it
# http://search.cpan.org/~mlehmann/JSON-XS-2.26/XS.pm#A_FEW_NOTES_ON_UNICODE_AND_PERL

# Twitter produces illegal \uffff in its JSON
$_ = decode("utf8", $_, Encode::FB_DEFAULT);

my $twit;
eval {
	$twit = $json->utf8->decode($_);
};
if ($@) { print STDERR "*** json DECODING error: $@, skipping: [\n$_]\n"; $_ = ""; next };

pretwit($twit);
if (my $rt = $twit->{retweeted_status}) {
	pretwit($rt);
}

#while (my($k, $v) = each (%$twit)){print "$k => $v\n";}

eval {
 	# $_ = $json->canonical->utf8->encode($twit)."\n";
 	# TODO there should be a prettier way to reduce $j into the below:
 	$_ = "$twit->{screen_name}\t$twit->{in_reply_to_screen_name}\t$twit->{created_at}\t$twit->{text}";
};
if ($@) { print STDERR "*** json ENCODING error: $@, skipping: [\n$_]\n"; $_ = ""; next };

sub pretwit {
	my $t = shift @_;
	my $user_id = $t->{user}->{id};
	my $screen_name = $t->{user}->{screen_name};
	delete $t->{user};
	$t->{user_id} = $user_id;
	$t->{screen_name} = $screen_name;

	$t->{created_at} = iso_date($t->{created_at});
	
}

sub iso_date {
	# always return properly-formatted date?
	my $res = "0000-00-00 00:00:00";
	if (@_ > 0) {
		my $x = shift @_;
		@iso = strptime($x);
		eval {
			$res = strftime('%Y-%m-%d %T',@iso);
		};
		if ($@) {
		}
	}
	$res
}
