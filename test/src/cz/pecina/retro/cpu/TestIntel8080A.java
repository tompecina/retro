/* TestIntel8080A.java
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

import junit.framework.TestCase;

public class TestIntel8080A extends TestCase {

  // CRC table
  private static final long CRC_TABLE[] = {
    0x00000000, 0x96300777, 0x2c610eee, 0xba510999,
    0x19c46d07, 0x8ff46a70, 0x35a563e9, 0xa395649e,
    0x3288db0e, 0xa4b8dc79, 0x1ee9d5e0, 0x88d9d297,
    0x2b4cb609, 0xbd7cb17e, 0x072db8e7, 0x911dbf90,
    0x6410b71d, 0xf220b06a, 0x4871b9f3, 0xde41be84,
    0x7dd4da1a, 0xebe4dd6d, 0x51b5d4f4, 0xc785d383,
    0x56986c13, 0xc0a86b64, 0x7af962fd, 0xecc9658a,
    0x4f5c0114, 0xd96c0663, 0x633d0ffa, 0xf50d088d,
    0xc8206e3b, 0x5e10694c, 0xe44160d5, 0x727167a2,
    0xd1e4033c, 0x47d4044b, 0xfd850dd2, 0x6bb50aa5,
    0xfaa8b535, 0x6c98b242, 0xd6c9bbdb, 0x40f9bcac,
    0xe36cd832, 0x755cdf45, 0xcf0dd6dc, 0x593dd1ab,
    0xac30d926, 0x3a00de51, 0x8051d7c8, 0x1661d0bf,
    0xb5f4b421, 0x23c4b356, 0x9995bacf, 0x0fa5bdb8,
    0x9eb80228, 0x0888055f, 0xb2d90cc6, 0x24e90bb1,
    0x877c6f2f, 0x114c6858, 0xab1d61c1, 0x3d2d66b6,
    0x9041dc76, 0x0671db01, 0xbc20d298, 0x2a10d5ef,
    0x8985b171, 0x1fb5b606, 0xa5e4bf9f, 0x33d4b8e8,
    0xa2c90778, 0x34f9000f, 0x8ea80996, 0x18980ee1,
    0xbb0d6a7f, 0x2d3d6d08, 0x976c6491, 0x015c63e6,
    0xf4516b6b, 0x62616c1c, 0xd8306585, 0x4e0062f2,
    0xed95066c, 0x7ba5011b, 0xc1f40882, 0x57c40ff5,
    0xc6d9b065, 0x50e9b712, 0xeab8be8b, 0x7c88b9fc,
    0xdf1ddd62, 0x492dda15, 0xf37cd38c, 0x654cd4fb,
    0x5861b24d, 0xce51b53a, 0x7400bca3, 0xe230bbd4,
    0x41a5df4a, 0xd795d83d, 0x6dc4d1a4, 0xfbf4d6d3,
    0x6ae96943, 0xfcd96e34, 0x468867ad, 0xd0b860da,
    0x732d0444, 0xe51d0333, 0x5f4c0aaa, 0xc97c0ddd,
    0x3c710550, 0xaa410227, 0x10100bbe, 0x86200cc9,
    0x25b56857, 0xb3856f20, 0x09d466b9, 0x9fe461ce,
    0x0ef9de5e, 0x98c9d929, 0x2298d0b0, 0xb4a8d7c7,
    0x173db359, 0x810db42e, 0x3b5cbdb7, 0xad6cbac0,
    0x2083b8ed, 0xb6b3bf9a, 0x0ce2b603, 0x9ad2b174,
    0x3947d5ea, 0xaf77d29d, 0x1526db04, 0x8316dc73,
    0x120b63e3, 0x843b6494, 0x3e6a6d0d, 0xa85a6a7a,
    0x0bcf0ee4, 0x9dff0993, 0x27ae000a, 0xb19e077d,
    0x44930ff0, 0xd2a30887, 0x68f2011e, 0xfec20669,
    0x5d5762f7, 0xcb676580, 0x71366c19, 0xe7066b6e,
    0x761bd4fe, 0xe02bd389, 0x5a7ada10, 0xcc4add67,
    0x6fdfb9f9, 0xf9efbe8e, 0x43beb717, 0xd58eb060,
    0xe8a3d6d6, 0x7e93d1a1, 0xc4c2d838, 0x52f2df4f,
    0xf167bbd1, 0x6757bca6, 0xdd06b53f, 0x4b36b248,
    0xda2b0dd8, 0x4c1b0aaf, 0xf64a0336, 0x607a0441,
    0xc3ef60df, 0x55df67a8, 0xef8e6e31, 0x79be6946,
    0x8cb361cb, 0x1a8366bc, 0xa0d26f25, 0x36e26852,
    0x95770ccc, 0x03470bbb, 0xb9160222, 0x2f260555,
    0xbe3bbac5, 0x280bbdb2, 0x925ab42b, 0x046ab35c,
    0xa7ffd7c2, 0x31cfd0b5, 0x8b9ed92c, 0x1daede5b,
    0xb0c2649b, 0x26f263ec, 0x9ca36a75, 0x0a936d02,
    0xa906099c, 0x3f360eeb, 0x85670772, 0x13570005,
    0x824abf95, 0x147ab8e2, 0xae2bb17b, 0x381bb60c,
    0x9b8ed292, 0x0dbed5e5, 0xb7efdc7c, 0x21dfdb0b,
    0xd4d2d386, 0x42e2d4f1, 0xf8b3dd68, 0x6e83da1f,
    0xcd16be81, 0x5b26b9f6, 0xe177b06f, 0x7747b718,
    0xe65a0888, 0x706a0fff, 0xca3b0666, 0x5c0b0111,
    0xff9e658f, 0x69ae62f8, 0xd3ff6b61, 0x45cf6c16,
    0x78e20aa0, 0xeed20dd7, 0x5483044e, 0xc2b30339,
    0x612667a7, 0xf71660d0, 0x4d476949, 0xdb776e3e,
    0x4a6ad1ae, 0xdc5ad6d9, 0x660bdf40, 0xf03bd837,
    0x53aebca9, 0xc59ebbde, 0x7fcfb247, 0xe9ffb530,
    0x1cf2bdbd, 0x8ac2baca, 0x3093b353, 0xa6a3b424,
    0x0536d0ba, 0x9306d7cd, 0x2957de54, 0xbf67d923,
    0x2e7a66b3, 0xb84a61c4, 0x021b685d, 0x942b6f2a,
    0x37be0bb4, 0xa18e0cc3, 0x1bdf055a, 0x8def022d
  };

  // msbt
  private static final int MSBT = 0x103;
  
  // test groups
  private static final TestGroup[] TEST_GROUPS = {

    new TestGroup(
      0xff,
      9, 0, 0, 0, 0xc4a5, 0xc4c7, 0xd226, 0xa050,
      0x58ea, 0x8566, 0xc6, 0xde, 0x9bc9,
      0x30, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xffff, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0xa64b4714,
      "dad <b,d,h,sp>"),

    new TestGroup(
      0xff,
      0xc6, 0, 0, 0, 0x9140, 0x7e3c, 0x7a67, 0xdf6d,
      0x5b61, 0x0b29, 0x10, 0x66, 0x85b2,
      0x38, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x9e2f929e,
      "aluop nn"),

    new TestGroup(
      0xff,
      0x80, 0, 0, 0, 0xc53e, 0x573a, 0x4c4d, MSBT,
      0xe309, 0xa666, 0xd0, 0x3b, 0xadbb,
      0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0, 0,
      0x862c76cf,
      "aluop <b,c,d,e,h,l,m,a>"),

    new TestGroup(
      0xff,
      0x27, 0, 0, 0, 0x2141, 0x09fa, 0x1d60, 0xa559,
      0x8d5b, 0x9079, 0x04, 0x8e, 0x299d,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0x0c033fbb,
      "<daa,cma,stc,cmc>"),

    new TestGroup(
      0xff,
      0x3c, 0, 0, 0, 0x4adf, 0xd5d8, 0xe598, 0x8a2b,
      0xa7b0, 0x431b, 0x44, 0x5a, 0xd030,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x0e46b6ad,
      "<inr,dcr> a"),

    new TestGroup(
      0xff,
      0x04, 0, 0, 0, 0xd623, 0x432d, 0x7a61, 0x8180,
      0x5a86, 0x1e85, 0x86, 0x58, 0x9bbb,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x4513ed83,
      "<inr,dcr> b"),

    new TestGroup(
      0xff,
      0x03, 0, 0, 0, 0xcd97, 0x44ab, 0x8dc9, 0xe3e3,
      0x11cc, 0xe8a4, 0x02, 0x49, 0x2a4d,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xcd8792f7,
      "<inx,dcx> b"),

    new TestGroup(
      0xff,
      0x0c, 0, 0, 0, 0xd789, 0x0935, 0x055b, 0x9f85,
      0x8b27, 0xd208, 0x95, 0x05, 0x0660,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x1b72f6e5,
      "<inr,dcr> c"),

    new TestGroup(
      0xff,
      0x14, 0, 0, 0, 0xa0ea, 0x5fba, 0x65fb, 0x981c,
      0x38cc, 0xdebc, 0x43, 0x5c, 0x03bd,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x9a57b515,
      "<inr,dcr> d"),

    new TestGroup(
      0xff,
      0x13, 0, 0, 0, 0x342e, 0x131d, 0x28c9, 0x0aca,
      0x9967, 0x3a2e, 0x92, 0xf6, 0x9d54,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x01254e7f,
      "<inx,dcx> d"),

    new TestGroup(
      0xff,
      0x1c, 0, 0, 0, 0x602f, 0x4c0d, 0x2402, 0xe2f5,
      0xa0f4, 0xa10a, 0x13, 0x32, 0x5925,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x96b32acf,
      "<inr,dcr> e"),

    new TestGroup(
      0xff,
      0x24, 0, 0, 0, 0x1506, 0xf2eb, 0xe8dd, 0x262b,
      0x11a6, 0xbc1a, 0x17, 0x06, 0x2818,
      0x01, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x2c95b212,
      "<inr,dcr> h"),

    new TestGroup(
      0xff,
      0x23, 0, 0, 0, 0xc3f4, 0x07a5, 0x1b6d, 0x4f04,
      0xe2c2, 0x822a, 0x57, 0xe0, 0xc3e1,
      0x08, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xc0232b9f,
      "<inx,dcx> h"),

    new TestGroup(
      0xff,
      0x2c, 0, 0, 0, 0x8031, 0xa520, 0x4356, 0xb409,
      0xf4c1, 0xdfa2, 0xd1, 0x3c, 0x3ea2,
      0x01, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x56d357ff,
      "<inr,dcr> l"),

    new TestGroup(
      0xff,
      0x34, 0, 0, 0, 0xb856, 0x0c7c, 0xe53e, MSBT,
      0x877e, 0xda58, 0x15, 0x5c, 0x1f37,
      0x01, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xbd63e992,
      "<inr,dcr> m"),

    new TestGroup(
      0xff,
      0x33, 0, 0, 0, 0x346f, 0xd482, 0xd169, 0xdeb6,
      0xa494, 0xf476, 0x53, 0x02, 0x855b,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xab2f70d5,
      "<inx,dcx> sp"),

    new TestGroup(
      0xff,
      0x2a, MSBT & 0xff, MSBT >> 8, 0, 0x9863, 0x7830, 0x2077, 0xb1fe,
      0xb9fa, 0xabb8, 0x04, 0x06, 0x6015,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xffff, 0, 0, 0, 0, 0, 0, 0, 0,
      0xcbd5c3a9,
      "lhld nnnn"),
	
    new TestGroup(
      0xff,
      0x22, MSBT & 0xff, MSBT >> 8, 0, 0xd003, 0x7772, 0x7f53, 0x3f72,
      0x64ea, 0xe180, 0x10, 0x2d, 0x35e9,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0,
      0x264f86e8,
      "shld nnnn"),

    new TestGroup(
      0xff,
      0x01, 0, 0, 0, 0x5c1c, 0x2d46, 0x8eb9, 0x6078,
      0x74b1, 0xb30e, 0x46, 0xd1, 0x30cc,
      0x30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0xff, 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0x126ef4fc,
      "lxi <b,d,h,sp>,nnnn"),

    new TestGroup(
      0xff,
      0x0a, 0, 0, 0, 0xb3a8, 0x1d2a, 0x7f8e, 0x42ac,
      MSBT, MSBT, 0xc6, 0xb1, 0xef8e,
      0x10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0x5f1d822b,
      "ldax <b,d>"),

    new TestGroup(
      0xff,
      0x06, 0, 0, 0, 0xc407, 0xf49d, 0xd13d, 0x0339,
      0xde89, 0x7455, 0x53, 0xc0, 0x5509,
      0x38, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0x4420a7ea,
      "mvi <b,c,d,e,h,l,m,a>,nn"),

    new TestGroup(
      0xff,
      0x40, 0, 0, 0, 0x72a4, 0xa024, 0x61ac, MSBT,
      0x82c7, 0x718f, 0x97, 0x8f, 0xef8e,
      0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0xff, 0,
      0xee8cb510,
      "mov <bcdehla>,<bcdehla>"),

    new TestGroup(
      0xff,
      0x32, MSBT & 0xff, MSBT >> 8, 0, 0xfd68, 0xf4ec, 0x44a0, 0xb543,
      0x0653, 0xcdba, 0xd2, 0x4f, 0x1fd8,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0x72af57ed,
      "sta nnnn / lda nnnn"),

    new TestGroup(
      0xff,
      0x07, 0, 0, 0, 0xcb92, 0x6d43, 0x0a90, 0xc284,
      0x0c53, 0xf50e, 0x91, 0xeb, 0x40fc,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x3592d8e0,
      "<rlc,rrc,ral,rar>"),

    new TestGroup(
      0xff,
      0x02, 0, 0, 0, 0x0c3b, 0xb592, 0x6cff, 0x959e,
      MSBT, MSBT + 1, 0xc1, 0x21, 0xbde7,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0xffff, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0xe971042b,
      "stax <b,d>")
  };

  private static class TestGroup {

    public int flagMask, ins0_init, ins1_init, ins2_init, ins3_init,
      memOp_init, hliy_init, hlix_init, hl_init, de_init, bc_init,
      flags_init, acc_init, sp_init, ins0_inc, ins1_inc, ins2_inc,
      ins3_inc, memOp_inc, hliy_inc, hlix_inc, hl_inc, de_inc, bc_inc,
      flags_inc, acc_inc, sp_inc, ins0_shift, ins1_shift, ins2_shift,
      ins3_shift, memOp_shift, hliy_shift, hlix_shift, hl_shift, de_shift,
      bc_shift, flags_shift, acc_shift, sp_shift;
    public long crc;
    public String name;

    public TestGroup(final int flagMask, final int ins0_init,
      final int ins1_init, final int ins2_init, final int ins3_init,
      final int memOp_init, final int hliy_init, final int hlix_init,
      final int hl_init, final int de_init, final int bc_init,
      final int flags_init, final int acc_init, final int sp_init,
      final int ins0_inc, final int ins1_inc, final int ins2_inc,
      final int ins3_inc, final int memOp_inc, final int hliy_inc,
      final int hlix_inc, final int hl_inc, final int de_inc,
      final int bc_inc, final int flags_inc, final int acc_inc,
      final int sp_inc, final int ins0_shift, final int ins1_shift,
      final int ins2_shift, final int ins3_shift, final int memOp_shift,
      final int hliy_shift, final int hlix_shift, final int hl_shift,
      final int de_shift, final int bc_shift, final int flags_shift,
      final int acc_shift, final int sp_shift, final long crc,
      final String name) {

      this.flagMask = flagMask;
      this.ins0_init = ins0_init;
      this.ins1_init = ins1_init;
      this.ins2_init = ins2_init;
      this.ins3_init = ins3_init;
      this.memOp_init = memOp_init;
      this.hliy_init = hliy_init;
      this.hlix_init = hlix_init;
      this.hl_init = hl_init;
      this.de_init = de_init;
      this.bc_init = bc_init;
      this.flags_init = flags_init;
      this.acc_init = acc_init;
      this.sp_init = sp_init;
      this.ins0_inc = ins0_inc;
      this.ins1_inc = ins1_inc;
      this.ins2_inc = ins2_inc;
      this.ins3_inc = ins3_inc;
      this.memOp_inc = memOp_inc;
      this.hliy_inc = hliy_inc;
      this.hlix_inc = hlix_inc;
      this.hl_inc = hl_inc;
      this.de_inc = de_inc;
      this.bc_inc = bc_inc;
      this.flags_inc = flags_inc;
      this.acc_inc = acc_inc;
      this.sp_inc = sp_inc;
      this.ins0_shift = ins0_shift;
      this.ins1_shift = ins1_shift;
      this.ins2_shift = ins2_shift;
      this.ins3_shift = ins3_shift;
      this.memOp_shift = memOp_shift;
      this.hliy_shift = hliy_shift;
      this.hlix_shift = hlix_shift;
      this.hl_shift = hl_shift;
      this.de_shift = de_shift;
      this.bc_shift = bc_shift;
      this.flags_shift = flags_shift;
      this.acc_shift = acc_shift;
      this.sp_shift = sp_shift;
      this.crc = crc;
      this.name = name;
    }
  }

  @Override
  protected void setUp() {
  }

  public void testMode0_1() {
  }
}
