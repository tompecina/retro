#include <stdlib.h>
#include <stdio.h>

#define P 5
#define C 8
#define CP 0x8000
#define R 19

static int RES[R][2] = {
  {0,0},{0,1},{0,2},{0,3},{0,4},{0,5},
  {1,0},{1,1},{1,2},{1,3},{1,4},
  {2,0},{2,1},{2,2},{2,3},
  {3,0},{3,1},{3,2},
  {4,0}
};

void match(int a, int b, int *x, int *y) {
  int i, j, na, nb, pa, pb;
  *x = *y = 0;
  for (i = 0, pa = a, pb = b; i < P; i++) {
    if (!((pa ^ pb) & 7)) {
      (*x)++;
    }
    pa >>= 3;
    pb >>= 3;
  }
  for (i = 0; i < C; i++) {
    na = nb = 0;
    for (j = 0, pa = a, pb = b; j < P; j++) {
      if ((pa & 7) == i) {
	na++;
      }
      if ((pb & 7) == i) {
	nb++;
      }
      pa >>= 3;
      pb >>= 3;
    }
    if (na < nb) {
      *y += na;
    } else {
      *y += nb;
    }
  }
  *y -= *x;
}

void minimax(unsigned char *g, unsigned char *s, int *r) {
  int rem[P + 1][P + 1], i, j, x, y, rm, gm;
  unsigned char *p, *q;
  gm = CP + 1;
  for (i = 0, p = g; i < CP; i++, p++) {
    if (!*g) {
      for (x = 0; x <= P; x++) {
	for (y = 0; y <= P; y++) {
	  rem[x][y] = 0;
	}
      }
      for(j = 0, q = s; j < CP; j++, q++) {
	if (*q) {
	  match(i, j, &x, &y);
	  (rem[x][y])++;
	}
      }
      rm = 0;
      for (x = 0; x <= P; x++) {
	for (y = 0; y <= P; y++) {
	  if (rem[x][y] > rm) {
	    rm = rem[x][y];
	  }
	}
      }
    }
    if (rm < gm) {
      gm = rm;
      *r = i;
    }
    if ((rm == gm) && !s[*r] && s[i]) {
      *r = i;
    }
  }
}

typedef struct {
  int num;
  int guess;
  unsigned int final;
  void *next[R];
} node;

static int n = 0;

void procnode(unsigned char *g, unsigned char *s, node *p) {
  int gs, i, j, x, y, nn, ls;
  unsigned char ng[CP], ns[CP], *pg, *qg, *ps, *qs;
  node *q;
  p->num = n++;
  p->final = 0;
  minimax(g, s, &gs);
  p->guess = gs;
  for (i = 0; i < R; i++) {
    nn = 0;
    for (j = 0, pg = g, qg = ng, ps = s, qs = ns; j < CP; j++) {
      *qg++ = *pg++;
      if (*ps++) {
	match(gs, j, &x, &y);
	*qs = (RES[i][0] == x) && (RES[i][1] == y);
	if (*qs++) {
	  nn++;
	  ls = j;
	}
      } else {
	*qs++ = 0;
      }      
    }
    ng[gs] = 1;
    if (!nn) {
      p->next[i] = NULL;
    } else {
      q = malloc(sizeof(node));
      p->next[i] = q;
      if (nn == 1) {
	q->guess = ls;
	q->final = 1;
      } else {
	procnode(ng, ns, q);
      }
    }
  }
}

void listnode(node *p) {
  int i;
  node *q;
  printf("#%d:%05o ", p->num, p->guess);
  for (i = 0; i < R; i++) {
    q = p->next[i];
    if (q) {
      printf("(%d,%d)", RES[i][0], RES[i][1]);
      if (q->final) {
	printf("%05o ", q->guess);
      } else {
	printf("#%d ", q->num);
      }
    }
  }
  printf("\n");
  for (i = 0; i < R; i++) {
    q = p->next[i];
    if (q && !q->final) {
      listnode(q);
    }
  }
}

void main() {
  unsigned char g[CP], s[CP];
  int i, r;
  node *root;
  for (i = 0; i < CP; i++) {
    g[i] = 0;
    s[i] = 1;
  }
  root = malloc(sizeof(node));
  procnode(g, s, root);
  listnode(root);
}
