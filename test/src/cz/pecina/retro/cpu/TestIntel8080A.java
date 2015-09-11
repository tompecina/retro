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

import java.math.BigInteger;

import junit.framework.TestCase;

public class TestIntel8080A extends TestCase {

  // msbt
  private static final int MSBT = 0x0103;
  
  // test groups
  private static final TestGroup[] TEST_GROUPS = {

    new TestGroup(
      0xff,
      9, 0, 0, 0, 0xc4a5, 0xc4c7, 0xd226, 0xa050,
      0x58ea, 0x8566, 0xc6, 0xde, 0x9bc9,
      0x30, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0xffff, 0xffff, 0xffff, 0xd7, 0, 0xffff,
      0x14474b0a6L,
      "dad <b,d,h,sp>"),

    new TestGroup(
      0xff,
      0xc6, 0, 0, 0, 0x9140, 0x7e3c, 0x7a67, 0xdf6d,
      0x5b61, 0x0b29, 0x10, 0x66, 0x85b2,
      0x38, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x9e922f9eL,
      "aluop nn"),

    new TestGroup(
      0xff,
      0x80, 0, 0, 0, 0xc53e, 0x573a, 0x4c4d, MSBT,
      0xe309, 0xa666, 0xd0, 0x3b, 0xadbb,
      0x3f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0xff, 0, 0, 0, 0xffff, 0xffff, 0xd7, 0, 0,
      0xcf762c86L,
      "aluop <b,c,d,e,h,l,m,a>"),

    new TestGroup(
      0xff,
      0x27, 0, 0, 0, 0x2141, 0x09fa, 0x1d60, 0xa559,
      0x8d5b, 0x9079, 0x04, 0x8e, 0x299d,
      0x18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0xbb3f030cL,
      "<daa,cma,stc,cmc>"),

    new TestGroup(
      0xff,
      0x3c, 0, 0, 0, 0x4adf, 0xd5d8, 0xe598, 0x8a2b,
      0xa7b0, 0x431b, 0x44, 0x5a, 0xd030,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xadb6460eL,
      "<inr,dcr> a"),

    new TestGroup(
      0xff,
      0x04, 0, 0, 0, 0xd623, 0x432d, 0x7a61, 0x8180,
      0x5a86, 0x1e85, 0x86, 0x58, 0x9bbb,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x83ed1345L,
      "<inr,dcr> b"),

    new TestGroup(
      0xff,
      0x03, 0, 0, 0, 0xcd97, 0x44ab, 0x8dc9, 0xe3e3,
      0x11cc, 0xe8a4, 0x02, 0x49, 0x2a4d,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xf79287cdL,
      "<inx,dcx> b"),

    new TestGroup(
      0xff,
      0x0c, 0, 0, 0, 0xd789, 0x0935, 0x055b, 0x9f85,
      0x8b27, 0xd208, 0x95, 0x05, 0x0660,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xe5f6721bL,
      "<inr,dcr> c"),

    new TestGroup(
      0xff,
      0x14, 0, 0, 0, 0xa0ea, 0x5fba, 0x65fb, 0x981c,
      0x38cc, 0xdebc, 0x43, 0x5c, 0x03bd,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x15b5579aL,
      "<inr,dcr> d"),

    new TestGroup(
      0xff,
      0x13, 0, 0, 0, 0x342e, 0x131d, 0x28c9, 0x0aca,
      0x9967, 0x3a2e, 0x92, 0xf6, 0x9d54,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x7f4e2501L,
      "<inx,dcx> d"),

    new TestGroup(
      0xff,
      0x1c, 0, 0, 0, 0x602f, 0x4c0d, 0x2402, 0xe2f5,
      0xa0f4, 0xa10a, 0x13, 0x32, 0x5925,
      0x01, 0, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xcf2ab396L,
      "<inr,dcr> e"),

    new TestGroup(
      0xff,
      0x24, 0, 0, 0, 0x1506, 0xf2eb, 0xe8dd, 0x262b,
      0x11a6, 0xbc1a, 0x17, 0x06, 0x2818,
      0x01, 0, 0, 0, 0, 0, 0, 0xff00, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x12b2952cL,
      "<inr,dcr> h"),

    new TestGroup(
      0xff,
      0x23, 0, 0, 0, 0xc3f4, 0x07a5, 0x1b6d, 0x4f04,
      0xe2c2, 0x822a, 0x57, 0xe0, 0xc3e1,
      0x08, 0, 0, 0, 0, 0, 0, 0xf821, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x9f2b23c0L,
      "<inx,dcx> h"),

    new TestGroup(
      0xff,
      0x2c, 0, 0, 0, 0x8031, 0xa520, 0x4356, 0xb409,
      0xf4c1, 0xdfa2, 0xd1, 0x3c, 0x3ea2,
      0x01, 0, 0, 0, 0, 0, 0, 0xff, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xff57d356L,
      "<inr,dcr> l"),

    new TestGroup(
      0xff,
      0x34, 0, 0, 0, 0xb856, 0x0c7c, 0xe53e, MSBT,
      0x877e, 0xda58, 0x15, 0x5c, 0x1f37,
      0x01, 0, 0, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0x92e963bdL,
      "<inr,dcr> m"),

    new TestGroup(
      0xff,
      0x33, 0, 0, 0, 0x346f, 0xd482, 0xd169, 0xdeb6,
      0xa494, 0xf476, 0x53, 0x02, 0x855b,
      0x08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xf821,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xd7, 0, 0,
      0xd5702fabL,
      "<inx,dcx> sp"),

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

  // CRC table
  private static final long CRC_TABLE[] = {
    0x00000000L, 0x77073096L, 0xee0e612cL, 0x990951baL,
    0x076dc419L, 0x706af48fL, 0xe963a535L, 0x9e6495a3L,
    0x0edb8832L, 0x79dcb8a4L, 0xe0d5e91eL, 0x97d2d988L,
    0x09b64c2bL, 0x7eb17cbdL, 0xe7b82d07L, 0x90bf1d91L,
    0x1db71064L, 0x6ab020f2L, 0xf3b97148L, 0x84be41deL,
    0x1adad47dL, 0x6ddde4ebL, 0xf4d4b551L, 0x83d385c7L,
    0x136c9856L, 0x646ba8c0L, 0xfd62f97aL, 0x8a65c9ecL,
    0x14015c4fL, 0x63066cd9L, 0xfa0f3d63L, 0x8d080df5L,
    0x3b6e20c8L, 0x4c69105eL, 0xd56041e4L, 0xa2677172L,
    0x3c03e4d1L, 0x4b04d447L, 0xd20d85fdL, 0xa50ab56bL,
    0x35b5a8faL, 0x42b2986cL, 0xdbbbc9d6L, 0xacbcf940L,
    0x32d86ce3L, 0x45df5c75L, 0xdcd60dcfL, 0xabd13d59L,
    0x26d930acL, 0x51de003aL, 0xc8d75180L, 0xbfd06116L,
    0x21b4f4b5L, 0x56b3c423L, 0xcfba9599L, 0xb8bda50fL,
    0x2802b89eL, 0x5f058808L, 0xc60cd9b2L, 0xb10be924L,
    0x2f6f7c87L, 0x58684c11L, 0xc1611dabL, 0xb6662d3dL,
    0x76dc4190L, 0x01db7106L, 0x98d220bcL, 0xefd5102aL,
    0x71b18589L, 0x06b6b51fL, 0x9fbfe4a5L, 0xe8b8d433L,
    0x7807c9a2L, 0x0f00f934L, 0x9609a88eL, 0xe10e9818L,
    0x7f6a0dbbL, 0x086d3d2dL, 0x91646c97L, 0xe6635c01L,
    0x6b6b51f4L, 0x1c6c6162L, 0x856530d8L, 0xf262004eL,
    0x6c0695edL, 0x1b01a57bL, 0x8208f4c1L, 0xf50fc457L,
    0x65b0d9c6L, 0x12b7e950L, 0x8bbeb8eaL, 0xfcb9887cL,
    0x62dd1ddfL, 0x15da2d49L, 0x8cd37cf3L, 0xfbd44c65L,
    0x4db26158L, 0x3ab551ceL, 0xa3bc0074L, 0xd4bb30e2L,
    0x4adfa541L, 0x3dd895d7L, 0xa4d1c46dL, 0xd3d6f4fbL,
    0x4369e96aL, 0x346ed9fcL, 0xad678846L, 0xda60b8d0L,
    0x44042d73L, 0x33031de5L, 0xaa0a4c5fL, 0xdd0d7cc9L,
    0x5005713cL, 0x270241aaL, 0xbe0b1010L, 0xc90c2086L,
    0x5768b525L, 0x206f85b3L, 0xb966d409L, 0xce61e49fL,
    0x5edef90eL, 0x29d9c998L, 0xb0d09822L, 0xc7d7a8b4L,
    0x59b33d17L, 0x2eb40d81L, 0xb7bd5c3bL, 0xc0ba6cadL,
    0xedb88320L, 0x9abfb3b6L, 0x03b6e20cL, 0x74b1d29aL,
    0xead54739L, 0x9dd277afL, 0x04db2615L, 0x73dc1683L,
    0xe3630b12L, 0x94643b84L, 0x0d6d6a3eL, 0x7a6a5aa8L,
    0xe40ecf0bL, 0x9309ff9dL, 0x0a00ae27L, 0x7d079eb1L,
    0xf00f9344L, 0x8708a3d2L, 0x1e01f268L, 0x6906c2feL,
    0xf762575dL, 0x806567cbL, 0x196c3671L, 0x6e6b06e7L,
    0xfed41b76L, 0x89d32be0L, 0x10da7a5aL, 0x67dd4accL,
    0xf9b9df6fL, 0x8ebeeff9L, 0x17b7be43L, 0x60b08ed5L,
    0xd6d6a3e8L, 0xa1d1937eL, 0x38d8c2c4L, 0x4fdff252L,
    0xd1bb67f1L, 0xa6bc5767L, 0x3fb506ddL, 0x48b2364bL,
    0xd80d2bdaL, 0xaf0a1b4cL, 0x36034af6L, 0x41047a60L,
    0xdf60efc3L, 0xa867df55L, 0x316e8eefL, 0x4669be79L,
    0xcb61b38cL, 0xbc66831aL, 0x256fd2a0L, 0x5268e236L,
    0xcc0c7795L, 0xbb0b4703L, 0x220216b9L, 0x5505262fL,
    0xc5ba3bbeL, 0xb2bd0b28L, 0x2bb45a92L, 0x5cb36a04L,
    0xc2d7ffa7L, 0xb5d0cf31L, 0x2cd99e8bL, 0x5bdeae1dL,
    0x9b64c2b0L, 0xec63f226L, 0x756aa39cL, 0x026d930aL,
    0x9c0906a9L, 0xeb0e363fL, 0x72076785L, 0x05005713L,
    0x95bf4a82L, 0xe2b87a14L, 0x7bb12baeL, 0x0cb61b38L,
    0x92d28e9bL, 0xe5d5be0dL, 0x7cdcefb7L, 0x0bdbdf21L,
    0x86d3d2d4L, 0xf1d4e242L, 0x68ddb3f8L, 0x1fda836eL,
    0x81be16cdL, 0xf6b9265bL, 0x6fb077e1L, 0x18b74777L,
    0x88085ae6L, 0xff0f6a70L, 0x66063bcaL, 0x11010b5cL,
    0x8f659effL, 0xf862ae69L, 0x616bffd3L, 0x166ccf45L,
    0xa00ae278L, 0xd70dd2eeL, 0x4e048354L, 0x3903b3c2L,
    0xa7672661L, 0xd06016f7L, 0x4969474dL, 0x3e6e77dbL,
    0xaed16a4aL, 0xd9d65adcL, 0x40df0b66L, 0x37d83bf0L,
    0xa9bcae53L, 0xdebb9ec5L, 0x47b2cf7fL, 0x30b5ffe9L,
    0xbdbdf21cL, 0xcabac28aL, 0x53b39330L, 0x24b4a3a6L,
    0xbad03605L, 0xcdd70693L, 0x54de5729L, 0x23d967bfL,
    0xb3667a2eL, 0xc4614ab8L, 0x5d681b02L, 0x2a6f2b94L,
    0xb40bbe37L, 0xc30c8ea1L, 0x5a05df1bL, 0x2d02ef8dL
  };

  // CRC accumulator
  private long crc;

  // CPU
  private Intel8080A cpu;

  // memory
  private SimpleMemory mem;

  // RAM
  private byte[] ram;

  // inititialize CRC
  private void initCrc() {
    crc = 0xffffffffL;
  }

  // update CRC
  private void updCrc(final int data) {
    crc = (crc >> 8) ^ CRC_TABLE[(int)((crc ^ data) & 0xff)];
  }
  
  @Override
  protected void setUp() {
    cpu = new Intel8080A("TEST_CPU");
    mem = new SimpleMemory("TEST_MEM", 0, 0);
    cpu.setMemory(mem);
    ram = mem.memory;
  }

  // size
  private static final int SIZE = 20;

  // offsets
  private static final int
    SEN = 0,
    SP = 1,
    ACC = 3,
    FLAGS = 4,
    BC = 5,
    DE = 7,
    HL = 9,
    HLIX = 11,
    HLIY = 13,
    MEM_OP = 15,
    INS3 = 17,
    INS2 = 18,
    INS1 = 19,
    INS0 = 20;

  // get array of set bit positions
  private int[] getSetBitPositions(BigInteger x) {
    final int l = x.bitCount();
    final int[] r = new int[l];
    for (int i = 0; i < l; i++) {
      final int n = x.getLowestSetBit();
      r[i] = n;
      x = x.flipBit(n);
    }
    return r;
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
      temp[HLIY + 1] = (byte)(tg.hliy_init & 0xff);
      temp[HLIY] = (byte)(tg.hliy_init >> 8);
      temp[HLIX + 1] = (byte)(tg.hlix_init & 0xff);
      temp[HLIX] = (byte)(tg.hlix_init >> 8);
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
      temp[HLIY + 1] = (byte)(tg.hliy_inc & 0xff);
      temp[HLIY] = (byte)(tg.hliy_inc >> 8);
      temp[HLIX + 1] = (byte)(tg.hlix_inc & 0xff);
      temp[HLIX] = (byte)(tg.hlix_inc >> 8);
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
      temp[HLIY + 1] = (byte)(tg.hliy_shift & 0xff);
      temp[HLIY] = (byte)(tg.hliy_shift >> 8);
      temp[HLIX + 1] = (byte)(tg.hlix_shift & 0xff);
      temp[HLIX] = (byte)(tg.hlix_shift >> 8);
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
      BigInteger incMask, shiftMask = BigInteger.ZERO, work;

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

	if (shiftCounter == 1) {
	  for (int i = 0; i < workBytes.length; i++)
	    System.out.printf("%d: %02x%n", i, workBytes[i]);
	  System.out.printf("%08x%n", crc);
	}
	if (ram[MSBT - 4] != 0x76) {
	  cpu.setPC(MSBT - 4);
	  cpu.exec();
	}

	workBytes[MEM_OP + 2] = ram[MSBT];
	workBytes[MEM_OP + 1] = ram[MSBT + 1];
	workBytes[HLIY + 2] = workBytes[HLIX + 2] =
	  workBytes[HL + 2] = (byte)(cpu.getL());
	workBytes[HLIY + 1] = workBytes[HLIX + 1] =
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
	if (shiftCounter == 1) {
	  for (int i = 0; i < workBytes.length; i++)
	    System.out.printf("%d: %02x%n", i, workBytes[i]);
	  System.out.printf("%08x%n", crc);
	  fail();
	}


	incMask = BigInteger.ZERO;
	if (++incCounter == incNum) {
	  incCounter = 0;
	  if ((++shiftCounter == shiftLen) || (shiftLen == 0)) {
	    break;
	  }
	}
	
	for (int i = 0; i < incLen; i++) {
	  if (((incCounter >> i) & 1) == 1) {
	    incMask = incMask.flipBit(incPos[i]);
	  }
	}

	if (shiftLen > 0) {
	  shiftMask = BigInteger.ONE.shiftLeft(shiftPos[shiftCounter]);
	}

	
	work = init
	  .xor(incMask)
	  .xor(shiftMask);
	
	workBytes = work.toByteArray();
      }
      assertEquals(tg.name,
		   String.format("%08x", tg.crc),
		   String.format("%08x", crc));
    }
  }
}
