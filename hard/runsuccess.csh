#!/bin/csh
# usage: runsuccess initfile goalfile, where there is a solution
limit cputime 80
set testdir=~cs61bl/code/proj3/hard
/bin/rm -f /tmp/out$$
echo $1 " " $2
java Solver $testdir/$1 $testdir/$2 > /tmp/out$$
if ($status != 0) echo "*** incorrect status"
java Checker $testdir/$1 $testdir/$2 < /tmp/out$$
if ($status != 0) echo "*** incorrect solver output"
/bin/rm -f /tmp/out$$
echo " "
