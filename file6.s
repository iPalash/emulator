.main:
mov r0 ,1
mov r1,2
mov r2 , 7
add r0, r1,r2
add r0,r1,0xAB12
add r0,r1,-543
addu r0,r1,0xAB12
addu r0,r1,-543
addh r0,r1,0xAB12
addh r0,r1,-543
sub r0,r1,r2
sub r0,r1,0xAB12
sub r0,r1,-543
subu r0,r1,0xAB12
subu r0,r1,-543
subh r0,r1,0xAB12
subh r0,r1,-543
mul r0,r1,r2
mul r0,r1,0xAB12
mul r0,r1,-543
mulu r0,r1,0xAB12
mulu r0,r1,-543
mulh r0,r1,0xAB12
mulh r0,r1,-543
cmp r2 , 0
.print r2
.print r1
div r0,r1,r2
div r0,r1,0xAB12
div r0,r1,-543
divu r0,r1,0xAB12
divu r0,r1,-543
divh r0,r1,0xAB12
divh r0,r1,-543
mod r0,r1,r2
mod r0,r1,0xAB12
mod r0,r1,-543
modu r0,r1,0xAB12
modu r0,r1,-543
modh r0,r1,0xAB12
modh r0,r1,-543
cmp r1,r2
cmp r1,0xAB12
cmp r1,125
cmpu r1,0xAB12
cmpu r1,-543
cmph r1,0xAB12
cmph r1,-543
and r0,r1,r2
and r0,r1,0xAB12
and r0,r1,-543
andu r0,r1,0xAB12
andu r0,r1,-543
andh r0,r1,0xAB12
andh r0,r1,-543
or r0,r1,r2
or r0,r1,0xAB12
or r0,r1,-543
oru r0,r1,0xAB12
oru r0,r1,-543
orh r0,r1,0xAB12
orh r0,r1,-543
not r0,r2
not r0,0xAB12
not r0,-543
notu r0,0xAB12
notu r0,-543
noth r0,0xAB12
noth r0,-543
mov r0,r2
mov r0,0xAB12
mov r0,-543
movu r0,0xAB12
movu r0,-543
movh r0,0xAB12
movh r0,-543
lsl r0,r2,r1
lsl r0,r2,0x0002
lsl r0,r2,1
lsr r0,r2,r1
lsr r0,r2,0x0002
lsr r0,r2,1
asr r0,r2,r1
asr r0,r2,0x0002
asr r0,r2,1
.end:
.print r0