4 AD=-16326:POKE-15878,176:K=3.14159:GCLEAR:SCALE0,286,0,256
5 FORA=1TO193STEP96:FORB=1TO173STEP86:MOVEA,B
6 PLOTA+93,B
7 PLOTA+93,B+83
8 PLOTA,B+83
9 PLOTA,B
11 NEXTB,A:MOVE105,139
12 LABEL2,2;"PMD-85"
13 MOVE107,126
14 LABEL1,1;"RGB-GRAPHIC"
15 MOVE110,105
16 LABEL2,2;"BASIC"
17 MOVE3,195
21 PLOT91,195
22 MOVE48,176
23 PLOT48,251
24 POKEAD,64:FORF=-2.5*KTO2.5*KSTEPK/8:H=4+(F+2.5*K)*89/(5*K):MOVEH,195
25 PLOTH,195+(SIN(F)/F)*55
27 NEXTF:MOVE240,2
28 PLOT240,82
29 MOVE195,42
30 PLOT284,42
31 POKEAD,128:FORF=0TO4*K+.2STEPK/12:PLOT195+F*90/(4*K),38*SIN(F)+42
32 NEXTF:FORA=7TO84STEP8:MOVE1,A
33 PLOT94,A
34 NEXTA:FORB=7TO94STEP9:MOVEB,1
35 PLOTB,84
36 NEXTB:MOVE240,251
37 POKEAD,192:FORF=0TO2*KSTEPK/36:A=30*SIN(F):MOVE240+A,213
39 PLOT240+A,213+36*COS(F)
40 NEXTF:MOVE184,215
42 POKEAD,0:FORF=0TO2*KSTEPK/72:PLOT144+40*COS(5*F),215+35*SIN(3*F)
43 NEXTF:MOVE144,76
44 POKEAD,64:FORF=0TO26*KSTEPK/1.27:PLOT144+35*SIN(F),41+35*COS(F)
46 NEXTF:FORF=0TO2*KSTEPK/4:FORG=F-K/4TO2*KSTEPK/4
47 MOVE48+38*SIN(F),127+38*COS(F)
48 PLOT48+38*SIN(F+G),127+38*COS(F+G)
50 NEXTG,F:R=.1:MOVE240,127
52 FORF=0TO27.2STEPK/24:PLOT240+R*SIN(F),127+R*COS(F)
53 R=R+.2:NEXTF:POKE-15878,168:MOVE97,87
54 POKEAD,192:FILL82,77;1:PAUSE60:POKEAD,0:GOTO4
