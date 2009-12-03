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
 	$_ = $json->canonical->utf8->encode($twit)."\n";
};
if ($@) { print STDERR "*** json ENCODING error: $@, skipping: [\n$_]\n"; $_ = ""; next };

sub pretwit {
	my $t = shift @_;
	my $user = $t->{user};
	my $newt = {};
	
	$newt->{user_id}         = $user->{id};
	$newt->{screen_name}     = $user->{screen_name};
	$newt->{followers_count} = $user->{followers_count};
	$newt->{friends_count}   = $user->{friends_count};
	$newt->{statuses_count}  = $user->{statuses_count};
	$newt->{favorites_count} = $user->{favorites_count};
	
	%$t = %$newt;	
}
