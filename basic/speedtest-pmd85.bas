1 REM TESLA PMD 85 BASIC SPEED TEST
5 OUT 95,176: OUT 94,0: OUT 94,0
10 LET W=250: DIM F(W):LET P=1:LET A=3
20 LET F(P)=A:LET P=P+1:IF P>W THEN 70
30 LET A=A+2:LET X=1
40 LET S=A/F(X):IF S=INT(S) THEN 30
50 LET X=X+1:IF X>=P THEN 60
51 IF F(X)*F(X)<=A THEN 40
60 GOTO 20
70 PRINT 65536-INP(94)-256*INP(94)
