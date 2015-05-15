#!/bin/bash
#
#  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
#  (MOVES) Institute at the Naval Postgraduate School (NPS)
#  http://www.MovesInstitute.org and http://www.nps.edu
 
#  This file is part of Mmowgli.
  
#  Mmowgli is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  any later version.

#  Mmowgli is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.

#  You should have received a copy of the GNU General Public License
#  along with Mmowgli in the form of a file named COPYING.  If not,
#  see <http://www.gnu.org/licenses/>

# Peforms at least a perfunctory check on the kosherness
# of URLs posted to the game. Uses the Spamhaus XBL list,
# a list of hosts that are known to be hijacked. See
# http://www.spamhaus.org/xbl for details.
#
# Works by doing a reverse lookup of the IP address. 
# for example, dig +short 0.210.73.209.sbl.spamhaus.org
# where the numbers are the IP number, reversed;
# in this case we are checking the IP 209.73.210.0.
#
# Returns zero if the URL is ok, 1 if the url is bad.
#
# @author DMcG

# Bash function fo determine if a string is a valid IP number
function valid_ip()
{
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}

url=$1

 # There are a couple different kinds of URLs; only look at the ones that start with http
 if [[ $url == http* ]]; then
   # Extract the hostname portion of the URL
   hostname=`echo $url | awk -F/ '{print $3}'`

   # Do a nslookup on the hostname. This may return more than one IP.
   allip=`nslookup $hostname | grep Add | grep -v '#' | cut -f 2 -d ' '`

   # For URLs with more than one IP, we just take the first IP. Realistically the
   # only DNS names with multiple IPs are major sites like CNN and Yahoo, and those
   # are unlikely to be malware sites.
   set -- $allip
   anIp=$1

   # do a sanity check on the IP. If the IP is no damn good, just
   # punt and return pass for the URL. It's easier to do this than
   # to discover all the ways it might be right.

   if valid_ip $anIp; then
     status=good;
   else
     echo invalid ip $anIp, punting
     exit 0;
   fi

   # wikileaks IPs. NPS resolves wikileaks to 127.0.0.1, so punt on that, too. Doesn't
   # make sense in any event.

   blacklistIps="108.162.233.9 108.162.233.10 108.162.233.11 108.162.233.12 108.162.233.13 127.0.0.1"

   # Check to see if this is a link to wikileaks
   for blacklisted in $blacklistIps
   do
     if [[ $blacklisted == $anIp ]]; then
        exit 1    
     fi
   done

   # Split the IP number into an array of four octets
   ipComponents=$(echo $anIp | tr "." " ")
   a=($ipComponents)

   # The spamhaus XBL list works by doing a DNS lookup. The format for
   # looking up the ip 1.2.3.4 is dig +short 4.3.2.1.xbl.spamhaus.org
   # If no value is returned it's OK; if we get back 127.0.0.x it's a
   # bad actor.
   result=`/usr/bin/dig +short ${a[3]}.${a[2]}.${a[1]}.${a[0]}.xbl.spamhaus.org`

   if [[ $result == 127.0.0* ]]; then
     echo "A link, $url, was attempted to be uploaded to the mmowgli game."  > /tmp/hostileUrlMessage.txt
     /bin/mail -s "HOSTILE LINK ATTEMPTED UPLOAD MMOWGLI" mcgredo@nps.edu < /tmp/hostileUrlMessage.txt
     exit 1
   fi
 fi 
exit 0
