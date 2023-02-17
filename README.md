# Tubes 1 Strategi ALgoritma Kelompok subsetRPL10

## Table Of Contents
* [Penjelasan Singkat Algoritma](#penjelasan-singkat-algoritma)
* [Requirement Program](#requirement-program)
* [Command untuk Meng-compile Program](#command-untuk-meng-compile-program)
* [Identitas Pembuat](#identitas-pembuat)

## Penjelasan Singkat Algoritma
Langkah-langkah yang diimplementasikan sebagai berikut:
a.	Membuat sebuah list yang berisi bot-bot lawan terurut berdasarkan jarak antara bot tersebut dengan bot sendiri.

b.	Membuat sebuah list yang berisi objek-objek lain yang terdapat dalam permainan, terurut berdasarkan nilai profit.

c.	Membuat sebuah list yang berisi bot-bot lawan yang berada pada jangkauan yang dekat. Jika list tersebut memiliki isi, maka akan diperiksa apakah ada bot yang berukuran lebih besar. Jika ada, maka bot tersebut akan ditandai sebagai dangerousPlayer. Jika tidak ada, maka akan diambil bot yang terbesar dan ditandai sebagai largestEdiblePlayer.

d.	Jika semua bot lain berukuran lebih kecil, heading akan diatur sehingga menghadap ke largestEdiblePlayer dan aksi yang dilakukan adalah maju.

e.	Jika ada yang lebih besar, maka akan diperiksa ukuran bot. Apabila lebih besar dari 50 dan bot memiliki torpedoSalvo, maka bot akan menembakkan torpedosalvo ke arah lawan. Jika tidak, maka bot akan menyalakan afterburner untuk melarikan diri. Jika ukuran bot lebih kecil daripada 50, maka bot akan menghindar dari bot lawan tanpa afterburner.

f.	Jika afterburner aktif dan tidak ada bot yang perlu dihindari, maka aksi yang akan dilakukan bot adalah mematikan afterburner.

g.	Jika bot sudah berada dekat dengan tepi map, maka bot akan diatur supaya berjalan mengarah ke titik tengah map.

h.	Jika ukuran bot lebih besar dari 50 dan memiliki 5 torpedo salvo, maka bot akan menembak bot lain yang paling besar dengan torpedosalvo.

i.	Jika list objek memiliki isi, maka akan diperiksa nilai profit. Jika profit bernilai negatif, maka objek tersebut akan ditandai sebagai benda berbahaya (dangerousObj). Jika positif, maka akan dicari objek dengan nilai profit yang paling tinggi.

j.	Jika tidak ada objek dengan profit negatif yang terdeteksi, maka bot akan berjalan mengarah kepada mostProfitableObject. Jika ada, maka akan diperiksa objek berbahaya tersebut bertipe apa. Jika objek bertipe SUPERNOVABOMB, maka arah bot akan dibelokkan 90 derajat dan tetap berjalan. Jika objek tersebut bertipe torpedosalvo, maka bot akan dibelokkan, dan jika bot lebih besar daripada 50 dan memiliki shield, maka shield akan diaktifkan. Jika tidak, maka bot akan berjalan saja.

k.	Jika objek tersebut bertipe gas cloud dan bot sudah terperangkap dalam gascloud, maka bot akan berjalan mengarah ke titik tengah map (ini hanyalah pendekatan heuristik, karena biasanya gas cloud jarang terletak di tengah map). Jika tidak, maka bot akan berbelok 120 derajat. Jika objek tersebut bertipe lain, maka bot akan dibelokkan 120 derajat.

l.	Jika dari kasus-kasus sebelumnya tidak ada yang terpenuhi, maka bot akan berjalan saja sesuai dengan arah sebelumnya.

## Requirement Program
Berikut ini adalah beberapa requirement dasar untuk menjalankan program
- Java (minimal Java 11): https://www.oracle.com/java/technologies/downloads/#java
- IntelliJ IDEA: https://www.jetbrains.com/idea/
- NodeJS: https://nodejs.org/en/download/
- .Net Core: https://dotnet.microsoft.com/en-us/download/dotnet/3.1

## Command untuk Meng-compile Program
Program  dapat dicompile dengan mengetikkan `./run.bat` pada terminal.

## Identitas Pembuat
| NIM                         | Nama 
|-----------------------------|------------------------------
| 13521042                    | Kevin John Wesley Hutabarat
| 13521140                    | Ryan Samuel Chandra
| 13521170                    | Haziq Abiyyu Mahdy

