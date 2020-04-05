import java.io.*;
import java.util.ArrayList;

public class FormatFile {
    public static void main(String[] args) {
        TextRedactor textReader = new TextRedactor();
        ArrayList<Character> mas = textReader.readFileAndRefactor("text.txt");
        try {
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("result.txt"));
            for (Character ma : mas) {
                writer.write(ma);
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TextRedactor {

    ArrayList<Character> mas;
    int comment = 0;
    int phrase = 0;
    int level = 0;

    public ArrayList<Character> readFileAndRefactor(String filename){
        ArrayList<Character> mas;
        mas = readFile(filename);
        mas = deleteEnters(mas);
        mas = deleteSpaces(mas);
        mas = deleteSpacesBeforeEndOfLine(mas);
        mas = putSpacesBefore(mas);
        mas = putSpacesAfter(mas);
        mas = editLevels(mas);
        return mas;
    }

    public ArrayList<Character> readFile(String filename) {//функция считывания массива символов из файла
        mas= new ArrayList<>();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(filename));
            int c;
            while ((c = reader.read()) != -1) {
                mas.add((char) c);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mas;
    }

    public ArrayList<Character> deleteEnters(ArrayList<Character> mas) {//функция удаления переходов на след.строку в считанном массиве
        comment = 0;
        phrase = 0;
        for (int i = 0; i < mas.size(); i++) {
            commentOrPhraseCheck(mas, i);
            if (mas.get(i) == '\n' && comment == 0) {
                mas.remove(i);
                i--;
            }
        }
        return mas;
    }

    public ArrayList<Character> deleteSpaces(ArrayList<Character> mas) {//функция уаления пробелов(больше одного) в считанном тексте
        comment = 0;
        phrase = 0;
        for (int i = 1; i < mas.size() - 1; i++) {
            commentOrPhraseCheck(mas, i);
            if (mas.get(i) == ' ' && mas.get(i + 1) == ' ' && phrase % 2 == 0 && comment == 0) {
                mas.remove(i + 1);
                i--;
            }
        }
        return mas;
    }

    public ArrayList<Character> deleteSpacesBeforeEndOfLine(ArrayList<Character> mas) {//удаление пробелов перед символом ;
        comment = 0;
        phrase = 0;
        for (int i = 1; i < mas.size() - 1; i++) {
            commentOrPhraseCheck(mas, i);
            if (mas.get(i + 1) == ';' && mas.get(i) == ' ' && phrase % 2 == 0 && comment == 0) {
                mas.remove(i);
                i--;
            }
        }
        return mas;
    }

    public ArrayList<Character> putSpacesBefore(ArrayList<Character> mas) {//вставка пробелов перед арифметическими операциями
        comment = 0;
        phrase = 0;
        for (int i = 1; i < mas.size() - 1; i++) {
            commentOrPhraseCheck(mas, i);
            if ((mas.get(i + 1) == '{' || mas.get(i + 1) == '+' || mas.get(i + 1) == '-' || mas.get(i + 1) == '/' ||
                    mas.get(i + 1) == '*' || mas.get(i + 1) == '%' || mas.get(i + 1) == '!' || mas.get(i + 1) == '|' ||
                    mas.get(i + 1) == '<' || mas.get(i + 1) == '>') && mas.get(i) != ' ' && phrase % 2 == 0 && comment == 0) {
                mas.add(i + 1, ' ');
                i++;
            }
            if (mas.get(i + 1) == '=' && mas.get(i) != ' ' && mas.get(i) != '+' && mas.get(i) != '-' && mas.get(i) != '/' &&
                    mas.get(i) != '*' && mas.get(i) != '=' && mas.get(i) != '!') {
                mas.add(i + 1, ' ');
            }
        }
        return mas;
    }



    public ArrayList<Character> putSpacesAfter(ArrayList<Character> mas) {//вставка пробелов после арифметических операций
        comment = 0;
        phrase = 0;
        for (int i = 1; i < mas.size() - 1; i++) {
            commentOrPhraseCheck(mas, i);
            if ((mas.get(i) == '+' || mas.get(i) == '-' || mas.get(i) == '=' || mas.get(i) == '/' ||
                    mas.get(i) == '*' || mas.get(i) == '%' || mas.get(i) == '|' ||
                    mas.get(i) == '<' || mas.get(i) == '>' || mas.get(i) == ',') && mas.get(i + 1) != ' ' && phrase % 2 == 0 && comment == 0) {
                mas.add(i + 1, ' ');
                i++;
            }
        }
        return mas;
    }


    public ArrayList<Character> editLevels(ArrayList<Character> mas) {//расстановка отступов внутри блоков кода
        comment = 0;
        phrase = 0;
        for (int i = 1; i < mas.size(); i++) {
            commentOrPhraseCheck(mas, i);
            if (mas.get(i) == '{' && phrase % 2 == 0 && comment == 0) {
                mas.add(i + 1, '\n');
                level++;
                for (int j = 0; j < level; j++) {
                    mas.add(i + 2 + j, '\t');
                }
                i = i + 1 + level;
            }
            if (mas.get(i) == ';' && phrase % 2 == 0 && comment == 0) {
                mas.add(i + 1, '\n');
                for (int j = 0; j < level; j++) {
                    mas.add(i + 2 + j, '\t');
                }
                i = i + 1 + level;
            }
            if (mas.get(i) == '}' && phrase % 2 == 0 && comment == 0) {
                mas.add(i + 1, '\n');
                level--;
                for (int j = 0; j < level; j++) {
                    mas.add(i + 2 + j, '\t');
                }
                if (level == 0) {
                    mas.add(i + 2, '\n');
                }
                mas.remove(i - 1);
                i = i + level;
            }
        }
        return mas;
    }

    private void commentOrPhraseCheck(ArrayList<Character> mas, int i) {//проверка на нахождение символа внутри комментария или кавычек
        if (mas.get(i) == '"' && mas.get(i - 1) != '\\' && mas.get(i - 1) != '\'' && comment == 0) {
            phrase++;
        }
        if (mas.get(i) == '/' && mas.get(i + 1) == '*' && phrase % 2 == 0) {
            comment++;
        }
        if (mas.get(i) == '*' && mas.get(i + 1) == '/' && phrase % 2 == 0) {
            comment--;
        }
    }
}