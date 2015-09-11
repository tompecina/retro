/* TestZilogZ80.java
 *
 * Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
 *
 * This file is part of cz.pecina.retro, retro 8-bit computer emulators.
 *
 * This application is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.         
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.pecina.retro.cpu;

import java.math.BigInteger;

public class TestZilogZ80 extends ProcessorTest {

  // test groups
  private static final TestGroup[] TEST_GROUPS = {

    new TestGroup(
      0xff,
      0xed, 0x42, 0, 0, 0x832c, 0x4f88, 0xf22b, 0xb339,
      0x7e1f, 0x1563, 0xd3, 0x89, 0x465e,
      0, 0x38, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xffff, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0xd48ad519L,
      "<adc,sbc> hl,<bc,de,hl,sp>"),

    new TestGroup(
      0xff,
      0x09, 0, 0, 0, 0xc4a5, 0xc4c7, 0xd226, 0xa050,
      0x58ea, 0x8566, 0xc6, 0xde, 0x9bc9,
      0x30, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xffff, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0xd9a4ca05L,
      "add hl,<bc,de,hl,sp>"),

    new TestGroup(
      0xff,
      0xdd, 0x09, 0, 0, 0xddac, 0xc294, 0x635b, 0x33d3,
      0x6a76, 0xfa20, 0x94, 0x68, 0x36f5,
      0, 0x30, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0xffff, 0, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0xb1df8ec0L,
      "add ix,<bc,de,ix,sp>"),

    new TestGroup(
      0xff.
      0xfd, 0x09, 0, 0, 0xc7c2, 0xf407, 0x51c1, 0x3e96,
      0x0bf4, 0x510f, 0x92, 0x1e, 0x71ea,
      0, 0x30, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0xffff, 0, 0, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0x39c8589bL,
      "add iy,<bc,de,iy,sp>"),

    new TestGroup(
      0xff,
      0xc6, 0, 0, 0, 0x9140, 0x7e3c, 0x7a67, 0xdf6d,
      0x5b61, 0x0b29, 0x10, 0x66, 0x85b2,
      0x38, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x51c19c2eL,
      "aluop a,nn"),

    new TestGroup(
      0xff,
      0x80, 0, 0, 0, 0xc53e, 0x573a, 0x4c4d, MSBT,
      0xe309, 0xa666, 0xd0, 0x3b, 0xadbb,
      0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0, 0,
      0x06c7aa8eL,
      "aluop a,<b,c,d,e,h,l,(hl),a>"),

    new TestGroup(
      0xff,
      0xdd, 0x84, 0, 0, 0xd6f7, 0xc76e, 0xaccf, 0x2847,
      0x22dd, 0xc035, 0xc5, 0x38, 0x234b,
      0x20, 0x39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0, 0,
      0xa886cc44L,
      "aluop a,<ixh,ixl,iyh,iyl>"),

    new TestGroup(
      0xff,
      0xdd, 0x86, 0x01, 0, 0x90b7, MSBT - 1, MSBT - 1, 0x32fd,
      0x406e, 0xc1dc, 0x45, 0x6e, 0xe5fa,
      0x20, 0x38, 0, 0, 0, 0x01, 0x01, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xd3f2d74aL,
      "aluop a,(<ix,iy>+1)"),

    new TestGroup(
      0xff,
      0xdd, 0xcb, 0x01, 0x46, 0x2075, MSBT - 1, MSBT - 1, 0x3cfc,
      0xa79a, 0x3d74, 0x51, 0x27, 0xca14,
      0x20, 0, 0, 0x38, 0, 0, 0, 0, 0, 0, 0x53, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0y83534ee1L,
      "bit n,(<ix,iy>+1)"),

    new TestGroup(
      0xff,
      0xcb, 0x40, 0, 0, 0x3ef1, 0x9dfc, 0x7acc, MSBT,
      0xbe61, 0x7a86, 0x50, 0x24, 0x1998,
      0, 0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0x53, 0, 0,
      0, 0xff, 0, 0, 0,0xffff, 0xffff, 0, 0xff, 0,
      0x5e020e98L,
      "bit n,<b,c,d,e,h,l,(hl),a>"),

    new TestGroup(
      0xff,
      0xed, 0xa9, 0, 0, 0xc7b6, 0x72b4, 0x18f6, MSBT + 17,
      0x8dbd, 0x01, 0xc0, 0x30, 0x94a3,
      0, 0x10, 0, 0, 0, 0, 0, 0, 0, 0x10, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x134b622dL,
      "cpd<r>"),

    new TestGroup(
      0xff,
      0xed, 0xa1, 0, 0, 0x4d48, 0xaf4a, 0x906b, MSBT,
      0x4e71, 0x01, 0x93, 0x6a, 0x907c,
      0, 0x10, 0, 0, 0, 0, 0, 0, 0, 0x10, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x2da42d19L,
      "cpi<r>"),

    new TestGroup(
      0xff,
      0x27, 0, 0, 0, 0x2141, 0x09fa, 0x1d60, 0xa559,
      0x8d5b, 0x9079, 0x04, 0x8e, 0x299d,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0x6d2dd213L,
      "<daa,cpl,scf,ccf>"),

    new TestGroup(
      0xff,
      0x3c, 0, 0, 0, 0x4adf, 0xd5d8, 0xe598, 0x8a2b,
      0xa7b0, 0x431b, 0x44, 0x5a, 0xd030,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x81fa8100L,
      "<inc,dec> a"),

    new TestGroup(
      0xff,
      0x04, 0, 0, 0, 0xd623, 0x432d, 0x7a61, 0x8180,
      0x5a86, 0x1e85, 0x86, 0x58, 0x9bbb,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x77f35a73L,
      "<inc,dec> b"),

    new TestGroup(
      0xff,
      0x03, 0, 0, 0, 0xcd97, 0x44ab, 0x8dc9, 0xe3e3,
      0x11cc, 0xe8a4, 0x02, 0x49, 0x2a4d,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xd2ae3becL,
      "<inc,dec> bc"),

    new TestGroup(
      0xff,
      0x0c, 0, 0, 0, 0xd789, 0x0935, 0x055b, 0x9f85,
      0x8b27, 0xd208, 0x95, 0x05, 0x0660,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x1af612a7L,
      "<inc,dec> c"),

    new TestGroup(
      0xff,
      0x14, 0, 0, 0, 0xa0ea, 0x5fba, 0x65fb, 0x981c,
      0x38cc, 0xdebc, 0x43, 0x5c, 0x03bd,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xd146bf51L,
      "<inc,dec> d"),

    new TestGroup(
      0xff,
      0x13, 0, 0, 0, 0x342e, 0x131d, 0x28c9, 0x0aca,
      0x9967, 0x3a2e, 0x92, 0xf6, 0x9d54,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xaec6d42cL,
      "<inc,dec> de"),

    new TestGroup(
      0xff,
      0x1c, 0, 0, 0, 0x602f, 0x4c0d, 0x2402, 0xe2f5,
      0xa0f4, 0xa10a, 0x13, 0x32, 0x5925,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xca8c6ac2L,
      "<inc,dec> e"),

    new TestGroup(
      0xff,
      0x24, 0, 0, 0, 0x1506, 0xf2eb, 0xe8dd, 0x262b,
      0x11a6, 0xbc1a, 0x17, 0x06, 0x2818,
      0x01, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x560f955eL,
      "<inc,dec> h"),

    new TestGroup(
      0xff,
      0x23, 0, 0, 0, 0xc3f4, 0x07a5, 0x1b6d, 0x4f04,
      0xe2c2, 0x822a, 0x57, 0xe0, 0xc3e1,
      0x08, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xfc0d6d4ahL,
      "<inc,dec> hl"),

    new TestGroup(
      0xff,
      0xdd, 0x23, 0, 0, 0xbc3c, 0x0d9b, 0xe081, 0xadfd,
      0x9a7f, 0x96e5, 0x13, 0x85, 0x0be2,
      0, 0x08, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xa54dbe31L,
      "<inc,dec> ix"),

    new TestGroup(
      0xff,
      0xfd, 0x23, 0, 0, 0x9402, 0x637a, 0x3182, 0xc65a,
      0xb2e9, 0xabb4, 0x16, 0xf2, 0x6d05,
      0, 0x08, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x505d51a3L,
      "<inc,dec> iy"),

    new TestGroup(
      0xff,
      0x2c, 0, 0, 0, 0x8031, 0xa520, 0x4356, 0xb409,
      0xf4c1, 0xdfa2, 0xd1, 0x3c, 0x3ea2,
      0x01, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xa0a1b49fL,
      "<inc,dec> l"),

    new TestGroup(
      0xff,
      0x34, 0, 0, 0, 0xb856, 0x0c7c, 0xe53e, MSBT,
      0x877e, 0xda58, 0x15, 0x5c, 0x1f37,
      0x01, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x28295eceL,
      "<inc,dec> (hl)"),

    new TestGroup(
      0xff,
      0x33, 0, 0, 0, 0x346f, 0xd482, 0xd169, 0xdeb6,
      0xa494, 0xf476, 0x53, 0x02, 0x855b,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x5dacd527L,
      "<inc,dec> sp"),

    new TestGroup(
      0xff,
      0xdd, 0x34, 0x01, 0, 0xfa6e, MSBT - 1, MSBT - 1, 0x2c28,
      0x8894, 0x5057, 0x16, 0x33, 0x286f,
      0x20, 0x01, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x0b95a8eaL,
      "<inc,dec> (<ix,iy>+1)"),

    new TestGroup(
      0xff,
      0xdd, 0x24, 0, 0, 0xb838, 0x316c, 0xc6d4, 0x3e01,
      0x8358, 0x15b4, 0x81, 0xde, 0x4259,
      0, 0x01, 0, 0, 0, 0xff00, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x6f463662L,
      "<inc,dec> ixh"),

    new TestGroup(
      0xff,
      0xdd, 0x2c, 0, 0, 0x4d14, 0x7460, 0x76d4, 0x06e7,
      0x32a2, 0x213c, 0xd6,0xd7, 0x99a5,
      0, 0x01, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x027bef2cL,
      "<inc,dec> ixl"),

    new TestGroup(
      0xff,
      0xdd, 0x24, 0, 0, 0x2836, 0x9f6f, 0x9116, 0x61b9,
      0x82cb, 0xe219, 0x92, 0x73, 0xa98c,
      0, 0x01, 0, 0, 0xff00, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x2d966f3L,
      "<inc,dec> iyh"),

    new TestGroup(
      0xff,
      0xdd, 0x2c, 0, 0, 0sd7c6, 0x62d5, 0xa09e, 0x7039,
      0x3e7e, 0x9f12, 0x90, 0xd9, 0x220f,
      0, 0x01, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x36c11e75L,
      "<inc,dec> iyl"),




    
    new TestGroup(
      0xff,
      0x2a, MSBT & 0xff, MSBT >> 8, 0, 0x9863, 0x7830, 0x2077, 0xb1fe,
      0xb9fa, 0xabb8, 0x04, 0x06, 0x6015,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xffff, 0, 0, 0, 0, 0, 0, 0, 0,
      0xa9c3d5cbL,
      "lhld nnnn"),
	
    new TestGroup(
      0xff,
      0x22, MSBT & 0xff, MSBT >> 8, 0, 0xd003, 0x7772, 0x7f53, 0x3f72,
      0x64ea, 0xe180, 0x10, 0x2d, 0x35e9,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xffff, 0, 0, 0, 0, 0,
      0xe8864f26L,
      "shld nnnn"),

    new TestGroup(
      0xff,
      0x01, 0, 0, 0, 0x5c1c, 0x2d46, 0x8eb9, 0x6078,
      0x74b1, 0xb30e, 0x46, 0xd1, 0x30cc,
      0x30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0xff, 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0xfcf46e12L,
      "lxi <b,d,h,sp>,nnnn"),

    new TestGroup(
      0xff,
      0x0a, 0, 0, 0, 0xb3a8, 0x1d2a, 0x7f8e, 0x42ac,
      MSBT, MSBT, 0xc6, 0xb1, 0xef8e,
      0x10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0x2b821d5fL,
      "ldax <b,d>"),

    new TestGroup(
      0xff,
      0x06, 0, 0, 0, 0xc407, 0xf49d, 0xd13d, 0x0339,
      0xde89, 0x7455, 0x53, 0xc0, 0x5509,
      0x38, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0xeaa72044L,
      "mvi <b,c,d,e,h,l,m,a>,nn"),

    new TestGroup(
      0xff,
      0x40, 0, 0, 0, 0x72a4, 0xa024, 0x61ac, MSBT,
      0x82c7, 0x718f, 0x97, 0x8f, 0xef8e,
      0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0xff, 0,
      0x10b58ceeL,
      "mov <bcdehla>,<bcdehla>"),

    new TestGroup(
      0xff,
      0x32, MSBT & 0xff, MSBT >> 8, 0, 0xfd68, 0xf4ec, 0x44a0, 0xb543,
      0x0653, 0xcdba, 0xd2, 0x4f, 0x1fd8,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0xed57af72L,
      "sta nnnn / lda nnnn"),

    new TestGroup(
      0xff,
      0x07, 0, 0, 0, 0xcb92, 0x6d43, 0x0a90, 0xc284,
      0x0c53, 0xf50e, 0x91, 0xeb, 0x40fc,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xe0d89235L,
      "<rlc,rrc,ral,rar>"),

    new TestGroup(
      0xff,
      0x02, 0, 0, 0, 0x0c3b, 0xb592, 0x6cff, 0x959e,
      MSBT, MSBT + 1, 0xc1, 0x21, 0xbde7,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xffff, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0x2b0471e9L,
      "stax <b,d>")
  };

  // CPU
  private Intel8080A cpu;

  @Override
  protected void setUp() {
    super.setUp();
    cpu = new Intel8080A("TEST_CPU");
    cpu.setMemory(mem);
  }

  public void testExer() {
    for (TestGroup tg: TEST_GROUPS) {
      final byte[] temp = new byte[SIZE + 1];

      temp[INS0] = (byte)(tg.ins0_init);
      temp[INS1] = (byte)(tg.ins1_init);
      temp[INS2] = (byte)(tg.ins2_init);
      temp[INS3] = (byte)(tg.ins3_init);
      temp[MEM_OP + 1] = (byte)(tg.memOp_init & 0xff);
      temp[MEM_OP] = (byte)(tg.memOp_init >> 8);
      temp[IY + 1] = (byte)(tg.iy_init & 0xff);
      temp[IY] = (byte)(tg.iy_init >> 8);
      temp[IX + 1] = (byte)(tg.ix_init & 0xff);
      temp[IX] = (byte)(tg.ix_init >> 8);
      temp[HL + 1] = (byte)(tg.hl_init & 0xff);
      temp[HL] = (byte)(tg.hl_init >> 8);
      temp[DE + 1] = (byte)(tg.de_init & 0xff);
      temp[DE] = (byte)(tg.de_init >> 8);
      temp[BC + 1] = (byte)(tg.bc_init & 0xff);
      temp[BC] = (byte)(tg.bc_init >> 8);
      temp[FLAGS] = (byte)(tg.flags_init);
      temp[ACC] = (byte)(tg.acc_init);
      temp[SP + 1] = (byte)(tg.sp_init & 0xff);
      temp[SP] = (byte)(tg.sp_init >> 8);
      temp[SEN] = (byte)0x80;
      final BigInteger init = new BigInteger(1, temp);

      temp[INS0] = (byte)(tg.ins0_inc);
      temp[INS1] = (byte)(tg.ins1_inc);
      temp[INS2] = (byte)(tg.ins2_inc);
      temp[INS3] = (byte)(tg.ins3_inc);
      temp[MEM_OP + 1] = (byte)(tg.memOp_inc & 0xff);
      temp[MEM_OP] = (byte)(tg.memOp_inc >> 8);
      temp[IY + 1] = (byte)(tg.iy_inc & 0xff);
      temp[IY] = (byte)(tg.iy_inc >> 8);
      temp[IX + 1] = (byte)(tg.ix_inc & 0xff);
      temp[IX] = (byte)(tg.ix_inc >> 8);
      temp[HL + 1] = (byte)(tg.hl_inc & 0xff);
      temp[HL] = (byte)(tg.hl_inc >> 8);
      temp[DE + 1] = (byte)(tg.de_inc & 0xff);
      temp[DE] = (byte)(tg.de_inc >> 8);
      temp[BC + 1] = (byte)(tg.bc_inc & 0xff);
      temp[BC] = (byte)(tg.bc_inc >> 8);
      temp[FLAGS] = (byte)(tg.flags_inc);
      temp[ACC] = (byte)(tg.acc_inc);
      temp[SP + 1] = (byte)(tg.sp_inc & 0xff);
      temp[SP] = (byte)(tg.sp_inc >> 8);
      temp[SEN] = 0;
      final BigInteger inc = new BigInteger(1, temp);
      final int[] incPos = getSetBitPositions(inc);
      final int incLen = incPos.length;
      final int incNum = 1 << incLen;
      
      temp[INS0] = (byte)(tg.ins0_shift);
      temp[INS1] = (byte)(tg.ins1_shift);
      temp[INS2] = (byte)(tg.ins2_shift);
      temp[INS3] = (byte)(tg.ins3_shift);
      temp[MEM_OP + 1] = (byte)(tg.memOp_shift & 0xff);
      temp[MEM_OP] = (byte)(tg.memOp_shift >> 8);
      temp[IY + 1] = (byte)(tg.iy_shift & 0xff);
      temp[IY] = (byte)(tg.iy_shift >> 8);
      temp[IX + 1] = (byte)(tg.ix_shift & 0xff);
      temp[IX] = (byte)(tg.ix_shift >> 8);
      temp[HL + 1] = (byte)(tg.hl_shift & 0xff);
      temp[HL] = (byte)(tg.hl_shift >> 8);
      temp[DE + 1] = (byte)(tg.de_shift & 0xff);
      temp[DE] = (byte)(tg.de_shift >> 8);
      temp[BC + 1] = (byte)(tg.bc_shift & 0xff);
      temp[BC] = (byte)(tg.bc_shift >> 8);
      temp[FLAGS] = (byte)(tg.flags_shift);
      temp[ACC] = (byte)(tg.acc_shift);
      temp[SP + 1] = (byte)(tg.sp_shift & 0xff);
      temp[SP] = (byte)(tg.sp_shift >> 8);
      temp[SEN] = 0;
      final BigInteger shift = new BigInteger(1, temp);
      final int[] shiftPos = getSetBitPositions(shift);
      final int shiftLen = shiftPos.length;
      
      initCrc();

      int incCounter = 0, shiftCounter = 0;
      BigInteger incMask, shiftMask;

      byte[] workBytes = init.toByteArray();

      for (;;) {

	ram[MSBT - 4] = workBytes[INS0 + 1];
	ram[MSBT - 3] = workBytes[INS1 + 1];
	ram[MSBT - 2] = workBytes[INS2 + 1];
	ram[MSBT - 1] = workBytes[INS3 + 1];
	ram[MSBT] = workBytes[MEM_OP + 2];
	ram[MSBT + 1] = workBytes[MEM_OP + 1];
	cpu.setL(workBytes[HL + 2] & 0xff);
	cpu.setH(workBytes[HL + 1] & 0xff);
	cpu.setE(workBytes[DE + 2] & 0xff);
	cpu.setD(workBytes[DE + 1] & 0xff);
	cpu.setC(workBytes[BC + 2] & 0xff);
	cpu.setB(workBytes[BC + 1] & 0xff);
	cpu.setF(workBytes[FLAGS + 1] & 0xff);
	cpu.setA(workBytes[ACC + 1] & 0xff);
	cpu.setSP((workBytes[SP + 2] & 0xff) +
		  ((workBytes[SP + 1] & 0xff) << 8));

	if (ram[MSBT - 4] != 0x76) {
	  cpu.setPC(MSBT - 4);
	  cpu.exec();

	  workBytes[MEM_OP + 2] = ram[MSBT];
	  workBytes[MEM_OP + 1] = ram[MSBT + 1];
	  workBytes[IY + 2] = workBytes[IX + 2] =
	    workBytes[HL + 2] = (byte)(cpu.getL());
	  workBytes[IY + 1] = workBytes[IX + 1] =
	    workBytes[HL + 1] = (byte)(cpu.getH());
	  workBytes[DE + 2] = (byte)(cpu.getE());
	  workBytes[DE + 1] = (byte)(cpu.getD());
	  workBytes[BC + 2] = (byte)(cpu.getC());
	  workBytes[BC + 1] = (byte)(cpu.getB());
	  workBytes[FLAGS + 1] = (byte)(cpu.getF() & tg.flagMask);
	  workBytes[ACC + 1] = (byte)(cpu.getA());
	  workBytes[SP + 2] = (byte)(cpu.getSP() & 0xff);
	  workBytes[SP + 1] = (byte)(cpu.getSP() >> 8);
	  
	  for (int i = 0; i < (SIZE - 4); i++) {
	    updCrc(workBytes[SIZE - 3 - i] & 0xff);
	  }
	}

	if (++incCounter == incNum) {
	  incCounter = 0;
	  if ((++shiftCounter > shiftLen) || (shiftLen == 0)) {
	    break;
	  }
	}
		
	incMask = BigInteger.ZERO;
	for (int i = 0; i < incLen; i++) {
	  if (((incCounter >> i) & 1) == 1) {
	    incMask = incMask.flipBit(incPos[i]);
	  }
	}

	if ((shiftLen > 0) && (shiftCounter < shiftLen)) {
	  shiftMask = BigInteger.ONE.shiftLeft(shiftPos[shiftCounter]);
	} else {
	  shiftMask = BigInteger.ZERO;
	}

	workBytes = init.xor(incMask).xor(shiftMask).toByteArray();
      }
      assertEquals(tg.name,
		   String.format("%08x", tg.crc),
		   String.format("%08x", crc));
    }
  }
}
