
char presseq[] =  " PrESS = ",
     yourturn[] = "your turn",
     draw[] =     "  drAu   ",
     youlost[] =  "you 1oSt ",
     youwon[] =   " you uon ";

unsigned char disp[9];

unsigned char cur_r, cur_c;

char board[9];

main() {  
  unsigned char i, r, c, playerfirst, turn;
  init8255();
  splashscreen();
  asc2buf(presseq, disp);
  while (wfk(disp) != '=');
  for (playerfirst = 0; 1; playerfirst ^= 1) {
    for (r = 0; r < 3; r++) {
      for (c = 0; c < 3; c++) {
	clearsym(r, c);
      }
    }
    for (i = 0; i < 9; i++) {
      board[i] = 0;
    }
    cur_r = 0;
    cur_c = 0;
    for (turn = 0; (turn < 9) && !win(); turn++) {
      if ((turn + playerfirst) % 2) {
	playermove(board);
      } else {
	computermove(board);
      }
    }
    switch (win()) {
      case 0:
	asc2buf(draw, disp);
	break;
      case 1:
	asc2buf(youlost, disp);
	break;
      case 2:
	asc2buf(youwon, disp);
	break;
    }
    wfk(disp);
  }
}

unsigned char wins[24] = {
  0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 3, 6, 1, 4, 7, 2, 5, 8, 0, 4, 8, 2, 4, 6};

win() {
  unsigned char i, i3;
  for (i = 0; i < 8; i++) {
    i3 = i * 3;
    if ((board[wins[i3]] != 0) &&
	(board[wins[i3]] == board[wins[i3 + 1]]) &&
	(board[wins[i3]] == board[wins[i3 + 2]])) {
      return board[wins[i3]];
    }
  }
  return 0;
}

playermove() {
  unsigned char i, n, *p, m;
  char key;
  n = 0;
  for (i = 0, p = board; i < 9; i++, p++) {
    if (*p == 0) {
      n++;
      m = i;
    }
  }
  if (n == 1) {
    board[m] = -1;
    paintsym(m / 3, m % 3, 2);
    return;
  }
  setcursor(cur_r, cur_c);
  asc2buf(yourturn, disp);
  while (((key = wfk(disp)) != '=') || board[(cur_r * 3) + cur_c]) {
    switch (key) {
      case '9':
	if (cur_r) {
	  clearcursor(cur_r, cur_c);
	  cur_r--;
	  setcursor(cur_r, cur_c);
	}
	break;
      case 'a':
	if (cur_r && (cur_c != 2)) {
	  clearcursor(cur_r, cur_c);
	  cur_r--;
	  cur_c++;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '6':
	if (cur_c != 2) {
	  clearcursor(cur_r, cur_c);
	  cur_c++;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '2':
	if ((cur_r != 2) && (cur_c != 2)) {
	  clearcursor(cur_r, cur_c);
	  cur_r++;
	  cur_c++;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '1':
	if (cur_r != 2) {
	  clearcursor(cur_r, cur_c);
	  cur_r++;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '0':
	if ((cur_r != 2) && cur_c) {
	  clearcursor(cur_r, cur_c);
	  cur_r++;
	  cur_c--;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '4':
	if (cur_c) {
	  clearcursor(cur_r, cur_c);
	  cur_c--;
	  setcursor(cur_r, cur_c);
	}
	break;
      case '8':
	if (cur_r && cur_c) {
	  clearcursor(cur_r, cur_c);
	  cur_r--;
	  cur_c--;
	  setcursor(cur_r, cur_c);
	}
	break;
    }
  }
  board[(cur_r * 3) + cur_c] = -1;
  paintsym(cur_r, cur_c, 2);
  clearcursor(cur_r, cur_c);
}

extern unsigned char bmt[];

computermove() {
  unsigned char i, n, *p, m, b0, b1, b2;
  unsigned int q;
  n = 0;
  for (i = 0, p = board; i < 9; i++, p++) {
    if (*p == 0) {
      n++;
      m = i;
      *p = 1;
      if (win() == 1) {
	n = 0;
	break;
      }
      *p = 0;
    }
  }
  if (n > 1) {
    b0 = 0;
    for (i = 0, p = board; i < 4; i++, p++) {
      b0 |= (*p & 3) << (i * 2);
    }
    b1 = 0;
    for (i = 0; i < 4; i++, p++) {
      b1 |= (*p & 3) << (i * 2);
    }
    b2 = *p & 3;
    for (p = bmt; 1; p += 5) {
      if ((*p == b0) && (*(p + 1) == b1) && (*(p + 2) == b2)) {
	break;
      }
    }
    q = *(p + 3) + (*(p + 4) << 8);
    m = 0;
    while (!(q & 1)) {
      q >>= 1;
      m++;
    }
  }
  board[m] = 1;
  paintsym(m / 3, m % 3, 1);
}

splashscreen() {
  clearmatrix();
  paintgrid();
  paintsym(0, 0, 3);
  paintsym(0, 1, 4);
  paintsym(0, 2, 5);
  paintsym(1, 0, 6);
  paintsym(1, 1, 7);
  paintsym(1, 2, 8);
  paintsym(2, 0, 9);
  paintsym(2, 1, 10);
  paintsym(2, 2, 11);
}

#define RED 1
#define GREEN 2
#define YELLOW 3
#define WHITE 4

#define BASEPORT 0x1c

setled(unsigned char row, unsigned char col, unsigned char state) {
  outp(BASEPORT, row);
  outp(BASEPORT + 1, col);
  outp(BASEPORT + 2, state);
}

clearmatrix() {
  unsigned char r, c;
  for (r = 0; r < 32; r++) {
    for (c = 0; c < 32; c++) {
      setled(r, c, 0);
    }
  }
}

#define GRIDCOLOR WHITE

paintgrid() {
  unsigned char i;
  for (i = 0; i < 32; i++) {
    setled(i, 10, GRIDCOLOR);
    setled(i, 21, GRIDCOLOR);
    setled(10, i, GRIDCOLOR);
    setled(21, i, GRIDCOLOR);
  }
}

unsigned char symbols[1200] = {
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 0 = blank */
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 1 = red nought */
  0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
  0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
  0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
  0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
  0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
  0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
  0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
  0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 2 = green cross */
  0, 2, 0, 0, 0, 0, 0, 0, 2, 0,
  0, 0, 2, 0, 0, 0, 0, 2, 0, 0,
  0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
  0, 0, 2, 0, 0, 0, 0, 2, 0, 0,
  0, 2, 0, 0, 0, 0, 0, 0, 2, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 3 = red T */
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 4 = green I */
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 5 = yellow C */
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 0, 0, 0, 0, 0, 0, 0,
  0, 3, 3, 0, 0, 0, 0, 0, 0, 0,
  0, 3, 3, 0, 0, 0, 0, 0, 0, 0,
  0, 3, 3, 0, 0, 0, 0, 0, 0, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 6 = green T */
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 7 = yellow A */
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 0, 0, 0, 0, 3, 3, 0,
  0, 3, 3, 0, 0, 0, 0, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 0, 0, 0, 0, 3, 3, 0,
  0, 3, 3, 0, 0, 0, 0, 3, 3, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 8 = red C */
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
  0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
  0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
  0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 9 = yellow T */
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 3, 3, 3, 3, 3, 3, 3, 3, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 10 = red O */
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 0, 0, 0, 0, 1, 1, 0,
  0, 1, 1, 0, 0, 0, 0, 1, 1, 0,
  0, 1, 1, 0, 0, 0, 0, 1, 1, 0,
  0, 1, 1, 0, 0, 0, 0, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  /* 11 = green E */
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 2, 2, 0, 0, 0, 0, 0, 0, 0,
  0, 2, 2, 2, 2, 2, 2, 0, 0, 0,
  0, 2, 2, 2, 2, 2, 2, 0, 0, 0,
  0, 2, 2, 0, 0, 0, 0, 0, 0, 0,
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 2, 2, 2, 2, 2, 2, 2, 2, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};

paintsym(unsigned char row, unsigned char col, unsigned char sym) {
  unsigned char r, c;
  row *= 11;
  col *= 11;
  for (r = 0; r < 10; r++) {
    for (c = 0; c < 10; c++) {
      setled(row + r,
	     col + c,
	     symbols[(sym * 100) + (r * 10) + c]);
    }
  }
}

clearsym(unsigned char row, unsigned char col) {
  unsigned char r, c;
  row *= 11;
  col *= 11;
  for (r = 0; r < 10; r++) {
    for (c = 0; c < 10; c++) {
      setled(row + r, col + c, 0);
    }
  }
}

#define CURSORCOLOR 3

setcursor(unsigned char row, unsigned char col) {
  unsigned char i;
  row *= 11;
  col *= 11;
  for (i = 0; i < 10; i++) {
    setled(row, col + i, CURSORCOLOR);
    setled(row + i, col + 9, CURSORCOLOR);
    setled(row + 9, col + i, CURSORCOLOR);
    setled(row + i, col, CURSORCOLOR);
  }
}

clearcursor(unsigned char row, unsigned char col) {
  unsigned char i;
  row *= 11;
  col *= 11;
  for (i = 0; i < 10; i++) {
    setled(row, col + i, 0);
    setled(row + i, col + 9, 0);
    setled(row + 9, col + i, 0);
    setled(row + i, col, 0);
  }
}

