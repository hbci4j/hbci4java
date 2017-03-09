
/*  $Id: AccountCRCAlgs.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

import java.math.BigInteger;

/* Some changes suggested by Alexander Nittka (AN) */
public class AccountCRCAlgs
{
    public static boolean alg_00(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    public static boolean alg_01(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8, new int[] {1,7,3,1,7,3,1,7,3}, false);
        int crc = (10 - sum%10) % 10;
        return number[9] == crc;
    }

    public static boolean alg_02(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {2,9,8,7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc==11)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_03(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2},false);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    public static boolean alg_04(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc==11)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_05(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8, new int[] {1,3,7,1,3,7,1,3,7}, false);
        int crc = (10 - sum%10) % 10;
        return number[9] == crc;
    }

    public static boolean alg_06(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2}, false);
        int crc = 11 - sum%11;
        if (crc>9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_07(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {10,9,8,7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc==11)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_08(int[] blz, int[] number) 
    {
        boolean result=true;
        long    bigint=calculateIntFromNumber(number);
        if (bigint>=60000) {
            int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
            int crc=(10-sum%10)%10;
            result=(number[9]==crc);
        } 
        return result;
    }

    public static boolean alg_09(int[] blz, int[] number) 
    {
        return true;
    }

    public static boolean alg_10(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 0, 8, new int[] {10,9,8,7,6,5,4,3,2}, false);
        int crc = 11 - sum%11;
        if (crc>9)
            crc=0;
        return number[9] == crc;
    }

    public static boolean alg_11(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {10,9,8,7,6,5,4,3,2}, false);
        int crc = 11 - sum%11;
        if (crc==10)
            crc=9;
        else if (crc==11)
            crc=0;
        return number[9] == crc;
    }
    
    public static boolean alg_12(int[] blz, int[] number)
    {
        // this should never happen, because 12 is not used
        return false;
    }

    public static boolean alg_13(int[] blz, int[] number) 
    {
        int sum = addProducts(number,1,6,new int[] {1,2,1,2,1,2}, true);
        int crc=(10-sum%10) % 10;
        
        boolean ok=(number[7]==crc);
        if (!ok) {
            sum=addProducts(number,3,8,new int[] {1,2,1,2,1,2}, true);
            crc = (10-sum%10) % 10;
            ok=(number[9]==crc);
        }
        return ok;    
    }

    public static boolean alg_14(int[] blz, int[] number) 
    {
        int sum = addProducts(number,3,8,new int[] {7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc==11)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_15(int[] blz, int[] number) 
    {
        int sum = addProducts(number,5,8,new int[] {5,4,3,2}, false);
        int crc = 11-sum%11;
        if (crc>9)
            crc=0;
        return number[9] == crc;
    }

    public static boolean alg_16(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2}, false);
        int crc = 11-sum%11;
        if (crc==11)
            crc=0;
        return (crc==10 && number[8]==number[9]) || (crc!=10 && number[9]==crc);
    }

    public static boolean alg_17(int[] blz, int[] number) 
    {
        /* Die Spez. sagt hier, dass die Quersumme nur für die Stellen 2, 4 und
         * 6 gebildet werden soll. Da die anderen Stellen (1,3,5) jedoch den
         * Faktor 1 haben, KÖNNEN diese Produkte niemals zweistellig werden, so
         * dass wir einfach IMMER die Quersumme bilden */
        int sum = addProducts(number,1,6,new int[] {1,2,1,2,1,2}, true);
        sum--;
        int crc = (10 - sum%11) % 10;
        return number[7] == crc;
    }

    public static boolean alg_18(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8, new int[] {3,1,7,9,3,1,7,9,3}, false);
        int crc = (10 - sum%10) % 10;
        return number[9] == crc;
    }

    public static boolean alg_19(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {1,9,8,7,6,5,4,3,2}, false);
        int crc=11-sum%11;
        if (crc>9)
            crc=0;
        return number[9] == crc;
    }

    public static boolean alg_20(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {3,9,8,7,6,5,4,3,2}, false);
        int crc=11-sum%11;
        if (crc>9)
            crc=0;
        return number[9] == crc;
    }
    
    public static boolean alg_21(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
        int checksum=quersumme(sum,true);
        int crc=10-checksum;
        return number[9]==crc;
    }

    public static boolean alg_22(int[] blz, int[] number) 
    {
        /* Spez: Von den jeweiligen Produkten bleiben die Zehnerstellen [bei der
         * Addition] unberücksichtigt. --> Es ist egal, ob wir die 10er mit
         * addieren oder nicht - da danach eh wieder ein Modulo 10 gemacht wird,
         * fallen die am Ende raus */
        int sum=addProducts(number,0,8,new int[] {3,1,3,1,3,1,3,1,3}, false);
        int crc=10-(sum%10);
        return number[9]==crc;
    }

    public static boolean alg_23(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,5,new int[] {7,6,5,4,3,2}, false);
        int crc = 11-sum%11;
        if (crc==11)
            crc=0;
        return (crc==10 && number[5]==number[6]) || (crc!=10 && number[6]==crc);
    }

    // code by Gerd Balzuweit
    public static boolean alg_24(int[] blz, int[] number) 
    {
        int[] weights={1,2,3,1,2,3,1,2,3};
        int crc = 0;
        int idx = 0;
        
        switch (number[0]) {
            case 3:
            case 4:
            case 5:
            case 6:
                number[0] = 0;
                break;
                
            case 9:
                number[0] = number[1] = number[2] = 0;
                break;
            
            default:
                break;
        }
        
        for(int i=0; i<9; i++) {
            if(number[i] > 0) {
                idx = i;
                break; 
            }
        }
        
        for(int i=idx,j=0; i<9; i++,j++) {
            crc += ((weights[j] * number[i]) + weights[j]) % 11; 
        }
        
        return ((crc % 10) == number[9]);
    }


    public static boolean alg_25(int[] blz, int[] number) 
    {
        int sum = addProducts(number,1,8, new int[] {9,8,7,6,5,4,3,2}, false);
        int crc = 11-(sum%11);
        
        if (crc==10 && number[1]<8) {
            return false;
        }
        if (crc>9) {
            crc=0;
        }
        return number[9]==crc;
    }
    
    public static boolean alg_26(int[] blz, int[] number) 
    {
        int startpos=0;
        if (number[0]==0 && number[1]==0)
            startpos=2;
        
        int sum = addProducts(number,startpos,startpos+6,new int[] {2,7,6,5,4,3,2}, false);
        int crc = 11 - sum%11;
        if (crc>9)
            crc = 0;
        return number[startpos+7] == crc;
    }

    public static boolean alg_27(int[] blz, int[] number) 
    {
        boolean ok;
        
        if (number[0]==0) {
            int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
            int crc=(10-sum%10)%10;
            ok = (number[9]==crc);
            
        } else {
            int[][] trafo=new int[][] {
                {0,1,5,9,3,7,4,8,2,6},
                {0,1,7,6,9,8,3,2,5,4},
                {0,1,8,4,6,2,9,5,7,3},
                {0,1,2,3,4,5,6,7,8,9}
            };
            
            int sum=0;
            for (int i=0; i<9; i++) {
                sum += trafo[(8-i)%4][number[i]];
            }
            
            int crc=(10-(sum%10))%10;
            ok = (number[9]==crc);
        }
        
        return ok;
    }

    public static boolean alg_28(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,6,new int[] {8,7,6,5,4,3,2}, false);
        int crc=11-sum%11;
        if (crc>9)
            crc=0;
        return number[7]==crc;
    }
    
    public static boolean alg_29(int[] blz, int[] number)
    {
        int[][] transform=new int[][] {
                {0,1,5,9,3,7,4,8,2,6},
                {0,1,7,6,9,8,3,2,5,4},
                {0,1,8,4,6,2,9,5,7,3},
                {0,1,2,3,4,5,6,7,8,9}};
        
        int sum=0;
        for (int i=0;i<9;i++) {
            sum += transform[i&3][number[8-i]];
        }
        int crc = (10-(sum%10))%10;
        return number[9]==crc;
    }

    public static boolean alg_30(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,0,0,0,0,1,2,1,2}, false);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    public static boolean alg_31(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {1,2,3,4,5,6,7,8,9}, false);
        int crc=sum%11;
        return number[9]==crc;
    }

    public static boolean alg_32(int[] blz, int[] number) 
    {
        int sum=addProducts(number,3,8,new int[] {7,6,5,4,3,2}, false);
        int crc=11-sum%11;
        if (crc>9)
            crc=0;
        return number[9] == crc;
    }
    
    // TODO: hier weiter 

    public static boolean alg_33(int[] blz, int[] number) 
    {
        int sum = addProducts(number,4,8,new int[] {6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_34(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 0, 6, new int[] { 7, 9, 10, 5, 8, 4, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[7] == crc;
    }

    public static boolean alg_38(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 3, 8, new int[] { 9, 10, 5, 8, 4, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    /** @author AN */
    public static boolean alg_41(int[] blz, int[] number) 
    {
        int sum;
        if (number[3] == 9) {
            sum = addProducts(number, 3, 8, new int[] { 1, 2, 1, 2, 1, 2 }, true);
        } else {
            sum = addProducts(number, 0, 8, new int[] { 2, 1, 2, 1, 2, 1, 2, 1, 2 }, true);
        }
        int crc = (10 - sum % 10) % 10;
        return number[9] == crc;
    }
   
    /** @author AN */
    public static boolean alg_42(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 1, 8, new int[] { 9, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }
    
    /** @author AN */
    public static boolean alg_43(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 0, 8, new int[] { 9, 8, 7, 6, 5, 4, 3, 2,1 }, false);
        int crc = (10 - sum % 10) % 10;
        return number[9] == crc;
    }
    
    /** @author AN */
    public static boolean alg_51(int[] blz, int[] number) 
    {
        boolean ok;
        
        if (number[2] != 9) {
            // Method A
            int sum = addProducts(number, 3, 8, new int[] { 7, 6, 5, 4, 3, 2 }, false);
            int crc = 11 - sum % 11;
            if (crc > 9)
                crc = 0;
            ok = (number[9] == crc);

            if (!ok) {
                // Method B
                sum = addProducts(number, 4, 8, new int[] { 6, 5, 4, 3, 2 }, false);
                crc = 11 - (sum % 11);
                if (crc > 9)
                    crc = 0;
                ok = (number[9] == crc);
            }
            
            if (!ok) {
                // Method C
                sum = addProducts(number, 3, 8, new int[] {1, 2, 1, 2, 1, 2 }, true);
                crc = (10 - sum % 10) % 10;
                ok = (number[9] == crc);
            }
            
            if (!ok && number[9]<7) {
                // Method D
                sum = addProducts(number, 4, 8, new int[] { 6, 5, 4, 3, 2 }, false);
                crc = (7 - (sum % 7))%7;
                if (crc > 9)
                    crc = 0;
                ok = (number[9] == crc);
            }
            
        } else { 
            // Sachkonten - Ausnahmen
            // Variante 1
            int sum = addProducts(number, 2, 8, new int[] { 8,7,6,5,4,3,2 }, false);
            int crc = 11 - (sum % 11);
            if (crc>9)
                crc = 0;
            ok = (number[9] == crc);
            
            if (!ok) {
                //Variante 2
                sum = addProducts(number, 0, 8, new int[] { 10,9,8,7,6,5,4,3,2 }, false);
                crc = 11-(sum%11);
                if (crc>9)
                    crc = 0;
                ok = (number[9] == crc);
            }
        }
        return ok;
    }

    /** @author AN */
    // TODO: more tests
    public static boolean alg_52(int[] blz, int[] number) 
    {
        boolean ok;
        
        if (number[0]==9) {
            ok=alg_20(blz,number);
            
        } else {
            //Kontonummer muss 8-stellig sein (mgl: number[2] darf 0 sein, d.h. Kontonummer faengt mit 0 an
            if (number[0]!=0 || number[1]!=0 || number[2]==0) {
                ok=false;
                
            } else {
                int[] weights=new int[] { 4,2,1,6,3,7,9,10,5,8,4,2 };

                //ESER-Nummer bauen
                int[] eser=new int[12];

                int knstartindex=4;
                while (number[knstartindex]==0){knstartindex++;}

                // kontonummer -> eser
                int i=11;
                for (int j=9;j>=knstartindex; j--,i--) {
                    eser[i]=number[j];
                }
                
                //Pruefziffer number[3] muss 0 gesetzt werden
                int indexOfPruefziffer=i;
                eser[i--]=0;
                
                //erste Stelle der Kontonummer;
                eser[i--]=number[2];

                for(int j=7;j>3;j--,i--){
                    eser[i]=blz[j];
                }
                
                //Ende Eser-Nummer bauen
                int sum=addProducts(eser, 0,11,  weights, false);
                int crc=sum%11;

                int     factor=0;
                boolean found=false;
                
                while (!found && factor<10){
                    int tocheck=(crc+factor*weights[indexOfPruefziffer])%11;
                    if (tocheck==10) {
                        crc=factor;
                        found=true;
                    }
                    factor++;
                }
                
                if (found && number[3]==crc) {
                    ok=true;
                } else {
                    ok=false;
                }
            }
        }
        
        return ok;
    }

    /** @author AN */
    // TODO: more tests
    public static boolean alg_53(int[] blz, int[] number) 
    {
        boolean ok;
        
        if (number[0]==9) {
            ok=alg_20(blz,number);
            
        } else {
            //Kontonummer muss 9-stellig sein (mgl: number[1] darf 0 sein, d.h. Kontonummer faengt mit 0 an?)
            if (!(number[0]==0 &&number[1]!=0)) {
                ok=false;
                
            } else {
                int[] weights=new int[] { 4,2,1,6,3,7,9,10,5,8,4,2 };
        
                //ESER-Nummer bauen
                int[] eser=new int[12];
        
                int knstartindex=4;
                while(number[knstartindex]==0){knstartindex++;}
        
                int i=11;
                for (int j=9;j>=knstartindex; j--,i--) {
                    eser[i]=number[j];
                }
                //Pruefziffer number[3] muss 0 gesetzt werden
                int indexOfPruefziffer=i;
                eser[i--]=0;
        
                //erste Stelle der Kontonummer;
                eser[i--]=number[1];
        
                eser[i--]=blz[7];
                eser[i--]=number[2];
                eser[i--]=blz[5];
                eser[i--]=blz[4];
                //Ende ESER-Nummer bauen
        
                int sum=addProducts(eser, 0,11,  weights, false);
                int crc=sum%11;
        
                int factor=0;
                boolean found=false;
                while (!found && factor<10) {
                    int tocheck=(crc+factor*weights[indexOfPruefziffer])%11;
                    if (tocheck==10) {
                        crc=factor;
                        found=true;
                    }
                    factor++;
                }
                if (found && number[3]==crc) { 
                    ok=true;
                } else {
                    ok=false;
                }
            }
        }
        return ok;
    }

    
    /** @author AN */
    public static boolean alg_55(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 0, 8, new int[] { 8, 7, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }


    /** @author AN */
    public static boolean alg_56(int[] blz, int[] number) 
    {
        int sum=addProducts(number, 0, 8, new int[] { 4, 3, 2, 7, 6, 5, 4, 3, 2 }, false);
        int crc=11-sum%11;
        if (crc>9){
            if (number[0]==9){
                crc=crc-3;
            }
        }
        return number[9] == crc;
    }
    

    public static boolean alg_57(int[] blz, int[] number)
    {
        int first=number[0]*10 + number[1];

        // 00 am anfang ist ungültig
        if (first==0) {
            return false;
        }
        
        // beginnend mit 777777 oder 888888 ist gültig
        int x=number[0];
        if (x==7 || x==8) {
            boolean ok=true;
            for (int i=1;i<6;i++) {
                if (number[i]!=x) {
                    ok=false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        
        // beginnend mit 40, 50, 91, 99 immer OK
        if (first==40 || first==50 || first==91 || first==99) {
            return true;
        }
        
        // pruefziffer an zehnter stelle
        if (first==51 || first==55 || first==61 || (first>=64 && first<=66) ||
                first==66 || first==70 || first==73 || 
                (first>=75 && first<=82) || first==88 || first==94 || first==95) 
        {
            int sum=addProducts(number,0,8,new int[] {1,2,1,2,1,2,1,2,1}, true);
            int crc=(10-sum%10)%10;
            return number[9]==crc;
        }
        
        // pruefziffer an dritter stelle
        if ((first>=32 && first<=39) || (first>=41 && first<=49) || 
                (first>=52 && first<=54) || (first>=56 && first<=60) ||
                first==62 || first==63 || (first>=67 && first<=69) ||
                first==71 || first==72 || first==74 || (first>=83 && first<=87) ||
                first==89 || first==90 || first==92 || first==93 ||
                (first>=96 && first<=98)) 
        {
            int sum=addProducts(number,0,9,new int[] {1,2,0,1,2,1,2,1,2,1}, true);
            int crc=(10-sum%10)%10;
            return number[2]==crc;
        }
        
        // beginnend mit 01-31: 3-4 immer 01-12, 7-9 immer <500
        if (first>=01 && first<=31) {
            int second=number[2]*10 + number[3];
            return second>=01 && second<=12 && number[6]<5;
        }
        
        StringBuffer n=new StringBuffer();
        for (int i=0;i<10;i++) {
            n.append((char)(number[i]+'0'));
        }
        return n.equals("0185125434");
    }


    /** @author AN */
    public static boolean alg_60(int[] blz, int[] number) 
    {
        int sum=addProducts(number,2,8,new int[] {2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }
    

    public static boolean alg_61(int[] blz, int[] number) 
    {
        int crc;

        if (number[8]==8) {
            int sum=addProducts(number,0,9,new int[] {2,1,2,1,2,1,2,0,1,2},true);
            crc=(10-sum%10)%10;
        } else {
            int sum=addProducts(number,0,6,new int[] {2,1,2,1,2,1,2},true);
            crc=(10-sum%10)%10;
        }
        return number[7]==crc;
    }

    public static boolean alg_63(int[] blz, int[] number) 
    {
        boolean ok;
        
        if (number[0]!=0) {
            ok=false;
        } else if (number[1]==0 && number[2]==0) {
            int sum = addProducts(number, 3, 8, new int[] { 1, 2, 1, 2, 1, 2 }, true);
            int crc = (10-sum%10)%10;
            ok=(number[9] == crc);
        } else {
            int sum = addProducts(number, 1, 6, new int[] { 1, 2, 1, 2, 1, 2 }, true);
            int crc = (10 - sum % 10) % 10;
            ok=(number[7] == crc);
        }
        
        return ok;
    }

    public static boolean alg_65(int[] blz, int[] number) 
    {
        int crc;
        
        if (number[8]==9) {
            int sum=addProducts(number,0,9,new int[] {2,1,2,1,2,1,2,0,1,2},true);
            crc=(10-sum%10)%10;
        } else {
            int sum=addProducts(number,0,6,new int[] {2,1,2,1,2,1,2},true);
            crc=(10-sum%10)%10;
        }
        return number[7]==crc;
    }


    /** @author AN */
    // TODO: more tests
    public static boolean alg_68(int[] blz, int[] number) 
    {
        // 10-stellige
        if (number[0]!=0){
            if (number[3]!=9) {
                return false;
            }
            int sum=addProducts(number,3,8,new int[] {1,2,1,2,1,2}, true);
            int crc=(10-sum%10)%10;
            return number[9]==crc;
        }

        // 9-stellige Ausnahme (Ausnahme nur bei Variante 2??)
        if (number[1]==4){
            // nicht pruefbar
            return true;
        }

        // auf 6stelligkeit pruefen
        int sumfirstfive = 0;
        for (int i = 0; i <= 4; i++) {
            sumfirstfive += number[i];
        }       
        //mindestens 6-stellig
        if (sumfirstfive==0) {
            return false;
        }

        //Variante 1
        int sum=addProducts(number,1,8,new int[] {1,2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        if( number[9]==crc){return true;}

        //Variante 2
        sum=addProducts(number,1,8,new int[] {1,0,0,2,1,2,1,2}, true);
        crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    
    public static boolean alg_69(int[] blz, int[] number)
    {
        boolean variant1=true;
        
        if (number[0]==9) {
            if (number[1]==3) {
                return true;
            } else if (number[1]==7) {
                variant1=false;
            }
        }
        
        if (variant1) {
            int sum = addProducts(number,0,6,new int[] {8,7,6,5,4,3,2},false);
            int crc = 11 - sum%11;
            if (crc>9)
                crc=0;
            
            boolean ok=(number[7]==crc);
            if (ok) {
                return true;
            }
        }

        int[][] trafo={{0,1,5,9,3,7,4,8,2,6},
                       {0,1,7,6,9,8,3,2,5,4},
                       {0,1,8,4,6,2,9,5,7,3},
                       {0,1,2,3,4,5,6,7,8,9}};
        int sum=0;
        for (int i=9;i!=0;i--) {
            int digit=number[i-1];
            int translated=trafo[(9-i)%4][digit];
            sum+=translated;
        }
        int crc=10-(sum%10);
        return number[9]==crc;
    }

    public static boolean alg_70(int[] blz, int[] number) 
    {
        int crc;
        
        if (number[3]==5 || (number[3]==6 && number[4]==9)) {
            int sum = addProducts(number,3,8,new int[] {7,6,5,4,3,2},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
        } else {
            int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
        }
        return number[9]==crc;
    }

    
    /** @author AN */
    // TODO: more tests
    public static boolean alg_74(int[] blz, int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        if (number[9]==crc) {
            return true;
        }

        if (number[0]+number[1]+number[2]+number[3]==0&&number[4]!=0) {
            crc=(5-sum%5);
            return number[9]==crc;
        }
        
        return false;
    }
    
    
    public static boolean alg_76(int[] blz, int[] number) 
    {
        boolean ok=false;
        
        if (number[1]==0 && number[2]==0) {
            int sum = addProducts(number, 3, 6, new int[] { 5, 4, 3, 2 }, false);
            int crc = sum % 11;
            ok=number[7] == crc;
        } else if (number[1]==0) {
            int sum = addProducts(number, 2, 6, new int[] { 6, 5, 4, 3, 2 }, false);
            int crc = sum % 11;
            ok=number[7] == crc;
        } else {
            int sum = addProducts(number, 1, 6, new int[] { 7, 6, 5, 4, 3, 2 }, false);
            int crc = sum % 11;
            ok=number[7] == crc;
        }
        
        if (!ok) {
            if (number[3]==0 && number[4]==0) {
                int sum = addProducts(number, 5, 8, new int[] { 5, 4, 3, 2 }, false);
                int crc = sum % 11;
                ok=number[9] == crc;
            } else if (number[3]==0) {
                int sum = addProducts(number, 4, 8, new int[] { 6, 5, 4, 3, 2 }, false);
                int crc = sum % 11;
                ok=number[9] == crc;
            } else {
                int sum = addProducts(number, 3, 8, new int[] { 7, 6, 5, 4, 3, 2 }, false);
                int crc = sum % 11;
                ok=number[9] == crc;
            }
        }
        
        return ok;
    }

    
    /** @author AN */
    public static boolean alg_78(int[] blz, int[] number) 
    {
        if (number[0] + number[1]==0 && number[2]!=0) {
            // nicht pruefbar
            return true;
        }
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }
    

    /** @author AN */
    public static boolean alg_81(int[] blz, int[] number) 
    {
        if (number[2] == 9) {
            return alg_51(blz,number);
        }
        return alg_32(blz,number);
    }
    

    /** @author AN */
    public static boolean alg_82(int[] blz, int[] number) 
    {
        if (number[2] == 9 && number[3] == 9) {
            return alg_10(blz,number);
        }
        return alg_33(blz, number);
    }


    /** @author AN */
    public static boolean alg_85(int[] blz, int[] number) 
    {
        // Ausnahme
        if (number[2]==9 && number[3]==9) {
            int sum = addProducts(number,2,8,new int[] {8,7,6,5,4,3,2},false);
            int crc=11-(sum%11);
            if (crc>10)
                crc=0;
            return number[9]==crc;
        }

        // Methode A
        int sum = addProducts(number, 3, 8, new int[] { 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        if (number[9] == crc) { 
            return true; 
        }

        // Methode B
        if (alg_33(blz,number)) {
            return true;
        }

        //Methode C
        sum = addProducts(number,4,8,new int[] {6,5,4,3,2},false);
        crc=(7-(sum%7))%7;
        return number[9]==crc;
    }
    

    /** @author AN */
    public static boolean alg_86(int[] blz, int[] number) 
    {
        // Ausnahme
        if(number[2]==9) {
            return alg_51(blz,number);
        }
        
        // Methode A
        int sum=addProducts(number,3,8,new int[] {1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        if (number[9]==crc) { 
            return true;
        }
        
        // Methode B
        return alg_33(blz,number);
    }
    
    
    /** @author AN */
    // TODO: more tests
    public static boolean alg_87(int[] blz, int[] number)
    {
        // Ausnahme
        int[] temp = number.clone();
        if (number[2]==9) {
            return alg_51(blz,number);
        }

        // Methode A
        int[] tab1 = new int[] {0, 4, 3, 2, 6};
        int[] tab2 = new int[] {7, 1, 5, 9, 8};

        int i = 3;
        while (number[i]==0) {
            i++;
        }
        int c2 = (i+1)%2;
        int d2 = 0;
        int a5 = 0;
        int p;
        while (i<9) {
            switch (number[i]) {
                case 0:
                    number[i] = 5;
                    break;
                case 1:
                    number[i] = 6;
                    break;
                case 5:
                    number[i] = 10;
                    break;
                case 6:
                    number[i] = 1;
                    break;
            }
            if (c2==d2) {
                if (number[i]>5) {
                    if (c2==0&&d2==0) {
                        c2 = 1;
                        d2 = 1;
                        a5 = a5+6-(number[i]-6);
                    } else {
                        c2 = 0;
                        d2 = 0;
                        a5 = a5+number[i];
                    }
                } else {
                    if (c2==0&&d2==0) {
                        c2 = 1;
                        a5 = a5+number[i];
                    } else {
                        c2 = 0;
                        a5 = a5+number[i];
                    }
                }
            } else {
                if (number[i]>5) {
                    if (c2==0) {
                        c2 = 1;
                        d2 = 0;
                        a5 = a5-6+(number[i]-6);
                    } else {
                        c2 = 0;
                        d2 = 1;
                        a5 = a5-number[i];
                    }
                } else {
                    if (c2==0) {
                        c2 = 1;
                        a5 = a5-number[i];
                    } else {
                        c2 = 0;
                        a5 = a5-number[i];
                    }
                }
            }
            i++;
        }

        while (a5<0||a5>4) {
            if (a5>4) {
                a5 = a5-5;
            } else {
                a5 = a5+5;
            }
        }

        if (d2==0) {
            p = tab1[a5];
        } else {
            p = tab2[a5];
        }

        if (p==number[9]) {
            return true;
        } 
        
        if (number[3]==0) {
            if (p>4) {
                p = p-5;
            } else {
                p = p+5;
            }
            if (p==number[9]) {
                return true;
            }
        }
        // Ende Methode A

        number = temp;
        // Methode B
        if (alg_33(blz,number)) {
            return true;
        }

        // Methode C
        int sum = addProducts(number, 4, 8, new int[] {6, 5, 4, 3, 2}, false);
        int crc = (7-(sum%7))%7;
        return number[9]==crc;
    }



    public static boolean alg_88(int[] blz, int[] number) 
    {
        int sum=0;
        if (number[2]==9)
            sum=addProducts(number,2,8,new int[] {8,7,6,5,4,3,2}, false);
        else
            sum=addProducts(number,3,8,new int[] {7,6,5,4,3,2}, false);
        int crc=11-sum%11;
        if (crc> 9)
            crc=0;
        return number[9] == crc;
    }

    
    /** @author AN */
    public static boolean alg_90(int[] blz, int[] number) 
    {
        // Sachkonten: Methode F
        if (number[2]==9) {
            int sum = addProducts(number, 2, 8, new int[] {8, 7, 6, 5, 4, 3, 2}, false);
            int crc = 11-sum%11;
            if (crc>9) crc = 0;
            return number[9]==crc;
        }
        
        // sonst Kundenkonto
        // Methode A
        int sum = addProducts(number, 3, 8, new int[] {7, 6, 5, 4, 3, 2}, false);
        int crc = 11-sum%11;
        if (crc>9) crc = 0;
        if (number[9]==crc) {
            return true;
        }

        // Methode B
        sum = addProducts(number, 4, 8, new int[] {6, 5, 4, 3, 2}, false);
        crc = 11-sum%11;
        if (crc>9) crc = 0;
        if (number[9]==crc) {
            return true;
        }

        // Methode C
        /* TODO: einige kontonummern werden laut spez hier ausgeschlossen */
        sum = addProducts(number, 4, 8, new int[] {6, 5, 4, 3, 2}, false);
        crc = 7-sum%7;
        if (crc==7) {
            crc=0;
        }
        if (number[9]==crc) {
            return true;
        }

        // Methode D
        sum = addProducts(number, 4, 8, new int[] {6, 5, 4, 3, 2}, false);
        crc = 9-sum%9;
        if (crc==9) {
            crc=0;
        }
        if (number[9]==crc && number[9]!=9) {
            return true;
        }

        // Methode E
        sum = addProducts(number, 4, 8, new int[] {2, 1, 2, 1, 2}, false);
        crc = (10-sum%10)%10;
        return number[9]==crc;
    }
    

    public static boolean alg_91(int[] blz, int[] number) 
    {
        int sum = addProducts(number,0,5,new int[] {7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
            
        if (number[6]!=crc) {
            sum = addProducts(number,0,5,new int[] {2,3,4,5,6,7},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
                
            if (number[6]!=crc) {
                sum = addProducts(number,0,9,new int[] {10,9,8,7,6,5,0,4,3,2},false);
                crc=11-(sum%11);
                if (crc>9)
                    crc=0;
            }
        }
        
        return number[6]==crc;
    }
    
    
    /** @author AN */
    public static boolean alg_92(int[] blz, int[] number) 
    {
        int sum = addProducts(number, 3, 8, new int[] { 1, 7, 3, 1, 7, 3 }, false);
        int crc = (10 - sum % 10) % 10;
        return number[9] == crc;
    }
    

    public static boolean alg_95(int[] blz, int[] number)
    {
        if (number[0]==0) {
            long bigint=calculateIntFromNumber(number);
            if (bigint>=1 && bigint<=1999999 ||
                bigint>=9000000 && bigint<=25999999 ||
                bigint>=396000000 && bigint<=499999999 ||
                bigint>=700000000 && bigint<=799999999 ||
                bigint>=910000000 && bigint<=989999999                
                            )
            {
                return true;
            }
        }
        
        int sum = addProducts(number, 0, 8, new int[] {4,3,2,7,6,5,4,3,2}, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_96(int[] blz, int[] number) 
    {
        boolean ret = false;
        if (!(ret = alg_19(blz,number)) && !(ret = alg_00(blz,number))) {
            long bigint = 0;
            if (number[0] == 0)
                bigint = calculateIntFromNumber(number);
            if (bigint >= 1300000 && bigint <= 99399999)
                ret = true;
        }
        return ret;
    }

    
    public static boolean alg_99(int[] blz, int[] number) 
    {
        long bigint = 0;
        if (number[0] == 0)
            bigint = calculateIntFromNumber(number);
        boolean ret = true;
        if (bigint < 396000000 || bigint > 499999999) {
            int sum
                = addProducts(number, 0, 8,
                              new int[] { 4, 3, 2, 7, 6, 5, 4, 3, 2 }, false);
            int crc = 11 - sum % 11;
            if (crc > 9)
                crc = 0;
            ret = number[9] == crc;
        }
        return ret;
    }
    
    
    /** @author AN */
    public static boolean alg_A0(int[] blz, int[] number) 
    {
        // Ausnahme
        int sumfirstseven = 0;
        for (int i = 0; i<7; i++) {
            sumfirstseven += number[i];
        }
        if (sumfirstseven==0) {
            return true;
        }
        // Ende Ausnahme

        int sum = addProducts(number, 4, 8, new int[] {10, 5, 8, 4, 2}, false);
        int crc = (11-sum%11);
        if (crc>9) crc = 0;
        return number[9]==crc;
    }


    /** @author AN */
    public static boolean alg_A1(int[] blz, int[] number) 
    {
        if (number[0]!=0 || (number[0]==0 && number[1]==0 && number[2]!=0)){
                int sum=addProducts(number,0,8,new int[] {0,0,2,1,2,1,2,1,2}, true);
            int crc=(10-sum%10)%10;
            return number[9]==crc;
        }
        return false;
    }
    
    
    /** @author AN */
    public static boolean alg_A2(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_00(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_04(blz,number);
    }

    /** @author AN */
    public static boolean alg_A3(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_00(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_10(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_A5(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_00(blz,number)) {
            return true;
        }
        if (number[0]==9) {
            return false;
        }
        // Variante 2
        return alg_10(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_A6(int[] blz, int[] number)
    {
        if (number[1]==8) {
            return alg_00(blz,number);
        }
        return alg_01(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_A7(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_00(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_03(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_A8(int[] blz, int[] number)
    {
        // Ausnahmen Sachkonto
        if (number[2]==9) {
            return alg_51(blz,number);
        }
        // Variante 1
        int sum = addProducts(number, 3, 8, new int[] {7, 6, 5, 4, 3, 2}, false);
        int crc = 11-sum%11;
        if (crc>9) crc = 0;
        if (number[9]==crc) {
            return true;
        }

        // Variante 2
        sum = addProducts(number, 3, 8, new int[] {1, 2, 1, 2, 1, 2}, true);
        crc = (10-sum%10)%10;
        return number[9]==crc;
    }

    
    /** @author AN */
    public static boolean alg_A9(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_01(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_06(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_B1(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_05(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_01(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_B3(int[] blz, int[] number)
    {
        if (number[0]!=9) {
            return alg_32(blz,number);
        }
        return alg_06(blz,number);
    }
    

    /** @author AN */
    public static boolean alg_B5(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_05(blz,number)) {
            return true;
        }
        // Variante 2
        return alg_00(blz,number);
    }

    
    /** @author AN */ 
    public static boolean alg_B6(int[] blz, int[] number)
    {
        // Variante 1
        if (number[0]!=0) {
            return alg_20(blz,number);
        }

        // Variante 2
        return alg_53(blz,number);
    }


    public static boolean alg_B8(int[] blz, int[] number)
    {
        int sum = addProducts(number, 0, 8,
                new int[] { 3, 9, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        boolean ret = (number[9] == crc);
        
        if (!ret) {
            ret = alg_29(blz,number);
        }
        
        return ret;
    }
    

    /** @author AN */
    public static boolean alg_C0(int[] blz, int[] number)
    {
        boolean ok = false;
        // Variante 1
        if (number[0]==0&&number[1]==0&&number[2]!=0) {
            ok = alg_52(blz, number);
        }
        if (ok) {
            return true;
        }

        // Variante 2
        return alg_20(blz,number);
    }

    
    /** @author AN */
    public static boolean alg_C1(int[] blz, int[] number)
    {
        // Variante 1
        if (number[0]!=5) {
            return alg_17(blz, number);
        }

        // Variante 2
        int sum=addProducts(number,0,8,new int[] {1,2,1,2,1,2,1,2,1}, true);
        sum--;
        int crc = (10-sum%11)%10;
        return number[9]==crc;
    }

    
    /** @author AN */
    public static boolean alg_C7(int[] blz, int[] number)
    {
        // Variante 1
        if (alg_63(blz,number)) {
            return true;
        }
        
        // TODO check!!
        // in der Beschreibung steht Methode 6 modifiziert, aber es ist nicht
        // klar an welcher Stelle
        // die Tests laufen auch mit der unmodifizierten Methode durch
        // Variante 2
        return alg_06(blz,number);
    }


    public static boolean alg_D0(int[] blz, int[] number)
    {
        boolean ok=false;
        
        if (number[0]==5 && number[1]==7) {
            ok = true;
        } else {
            int sum = addProducts(number, 0, 8, new int[] { 3, 9, 8, 7, 6, 5, 4, 3, 2 }, false);
            int crc = 11 - sum % 11;
            if (crc > 9)
                crc = 0;
            ok = (number[9] == crc);
        }
        
        return ok;
    }
    

    private static int addProducts(int[] number, int first, int last,
                                   int[] factors, boolean withChecksum) 
    {
        int result = 0;
        for (int i=first; i<=last; i++) {
            int prod = number[i] * factors[i-first];
            if (withChecksum)
                prod = quersumme(prod, false);
            result += prod;
        }
        return result;
    }
    
    private static int quersumme(int x, boolean recursive)
    {
        int sum=0;
        while (x>0) {
            sum+=x%10; // einerstelle addieren
            x/=10; // x eine stelle nach rechts "verschieben"
        }
        if (recursive&&sum>=10) {
            sum=quersumme(sum, true);
        }
        return sum;
    }

    private static long calculateIntFromNumber(int[] number) 
    {
        long bigint=0;
        for (int i=0; i<10; i++) {
            bigint*=10;
            bigint+=number[i];
        }
        return bigint;
    }
    
    public static boolean checkIBAN(String iban)
    {
    	StringBuffer s=new StringBuffer();
    	
    	s.append(iban.substring(4));
    	s.append(iban.substring(0,4));
    	
    	StringBuffer s2=new StringBuffer();
    	for (int i=0; i<s.length(); i++) {
    	    char ch=s.charAt(i);
    	    if (ch>='0' && ch<='9') {
    	        s2.append(ch);
    	    } else {
    	        s2.append(ch-'A'+10);
    	    }
    	}
    	
    	BigInteger x=new BigInteger(s2.toString());
    	BigInteger rest=x.mod(new BigInteger("97"));
    	
    	return rest.intValue()==1;
    }
    
    public static boolean checkCreditorId(String creditorId)
    {
        //DE: Immer Länge 18
        if ("DE".equals(creditorId.substring(0,2).toUpperCase()) && creditorId.length()!=18)
            return false;
                
        //Rest wie bei IBAN
        StringBuffer s=new StringBuffer();
        
        s.append(creditorId.substring(7));
        s.append(creditorId.substring(0,4));
        
        StringBuffer s2=new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char ch=s.charAt(i);
            if (ch>='0' && ch<='9') {
                s2.append(ch);
            } else {
                s2.append(ch-'A'+10);
            }
        }
        
        BigInteger x=new BigInteger(s2.toString());
        BigInteger rest=x.mod(new BigInteger("97"));
        
        return rest.intValue()==1;
    }
}
