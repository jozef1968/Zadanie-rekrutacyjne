# Zadanie rekrutacyjne
Rozbudowa 4 klas według załączonej listy wymagań

Zadanie 1
Należy pobrać projekt znajdujący się w repozytorium:
https://github.com/rmarzec/tests.git
A następnie uruchomić (np. w IntelliJ) przykładowy test (klasa com.gitlab.rmarzec.task.Task1Test). Do
uruchomienia projektu niezbędna jest java w wersji 1.8 oraz maven.

Zadanie 2
Należy rozbudować klasę com.gitlab.rmarzec.task.Task2Test aby realizowała scenariusz:
1. Otwarcie adresu https://pl.wikipedia.org/wiki/Wiki
2. Po prawej stronie u góry należy kliknąć przycisk związany z wyborem języka.
3. Należy pobrać wszystkie jako List<WebElement>
4. W pętli foreach wypisać nazwy wszystkich języków z listy utworzonej w pkt 3. Dla języka „English”
należy wyświetlić także jego adres url

Zadanie 3
Należy rozbudować klasę com.gitlab.rmarzec.task.Task3Test aby realizowała scenariusz:
1. Otwarcie adresu https://www.google.com/
2. Akceptacja ciasteczek
3. Wpisanie w polu wyszukiwania HTML select tag - W3Schools
4. Kliknięcie „Szczęśliwy traf”
5. Zweryfikowanie czy trafiliśmy na stronę https://www.w3schools.com/tags/tag_select.asp Jeśli nie,
w konsoli należy wypisać informację z aktualnym adresem oraz przejść na podany powyżej adres.
6. Akceptacja ciasteczek
7. Uruchomić pierwszy przykład przyciskiem Try it Yourself
8. Pobrać WebElement zawierający treść nagłówka (The select element)
9. Wypisać nagłówek w konsoli
10. Wybrać z rozwijanej listy „Opel”
11. Pobrać wybrany element do WebElement i wypisać w konsoli tekst oraz wartość „value” (Opel,
opel)

Zadanie 4
Należy rozbudować klasę com.gitlab.rmarzec.task.Task4Test aby realizowała scenariusz:
1. Otwarcie adresu https://www.youtube.com/
2. Akceptacja ciasteczek

3. Przejście na zakładkę „Shorts”
4. Wypisanie nazwy kanału wrzucającego film
5. Powrót na stronę główną
6. Wyszukanie po słowie kluczowym „Live”
7. Dla pierwszych 12 filmów (nie dotyczy Shorts) należy pobrać informację i zapisać w liście obiektów
List<YTTile>:
a) Tytuł
b) Nazwa kanału
c) Długość (jeśli jest to transmisja na żywo – wpisać wartość „live”)
8. Wypisać z utworzonej listy tytuł oraz czas trwania dla wszystkich filmów, które nie są
transmitowane na żywo.

Uwagi
1. W testach nie wolno wykorzystywać sleepów, tylko oczekiwanie na elementy
2. Należy zwrócić uwagę na czytelność kodu (składnia, nazewnictwo, łatwość zrozumienia)
3. Operacje powinny być wykonane w jak najprostszy sposób
4. Testy powinny działać stabilnie i być powtarzalne
