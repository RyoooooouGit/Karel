package scr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Save {
    Map map;
    String[] commandInFunction;
    String functionName, logOutput, wrongOutput, codeInTextArea;
    boolean ifOver, ifBegin, ifSuccess;

    public Save(int i) {
        map = new Map(i);
        logOutput = "Stage" + (i + 1) + " initialized!\n";
        wrongOutput = "";
        codeInTextArea = "";
        ifOver = false;
        ifBegin = false;
        ifSuccess = false;
    }

    private void action(String name, String variable) {
        // move() or move(int x)
        if (name.equals("move")) {
            int step = variable.equals("") ? 1 : Integer.parseInt(variable);
            for (int i = 0; i < step; i++) {
                if (map.checkIfHit()) {
                    break;
                }
                if (map.checkIfTrap()) {
                    ifOver = true;
                    map.karel.move();
                    break;
                }
                map.karel.move();
            }
        }

        // turnLeft()
        else if (name.equals("turnLeft") && variable.equals("")) {
            map.karel.turnLeft();
        }
        // pickRock()
        else if (name.equals("pickRock") && variable.equals("")) {
            map.pickRock();
            if (map.rockLeft == 0) {
                ifOver = true;
                ifSuccess = true;
            }
        }
        // showInformation()
        else if (name.equals("showInformation") && variable.equals("")) {
            map.showInformation();
        }
        // putRock()
        else if (name.equals("putRock") && variable.equals("")) {
            map.putRock();
        }
        // noRockPresent()
        else if (name.equals("noRockPresent") && variable.equals("")) {
            logOutput += (!map.checkIfRock() ? "true" : "false") + "\n";
        }
        // noRockInBag()
        else if (name.equals("noRockInBag") && variable.equals("")) {
            logOutput += (map.karel.rockInBag == 0 ? "true" : "false") + "\n";
        }
        if (!map.logOutput.equals("")) {
            logOutput += map.logOutput;
            map.logOutput = "";
        }
        if (!map.wrongOutput.equals("")) {
            wrongOutput += map.wrongOutput;
            map.wrongOutput = "";
        }
        if (ifOver) {
            if (ifSuccess) {
                logOutput += "Now you have picked all of the rocks.\n";
                logOutput += "You win the game. Congratulations!\n";
            } else {
                logOutput += "You are stepping into a trap.\n";
                logOutput += "You failed. Game over.\n";
            }
        }
    }

    private boolean ifBasic(String command) {
        String[] basicCommands = {
                "turnLeft", "pickRock", "showInformation", "putRock", "noRockPresent", "noRockInBag" };
        String[] basicCommandsWithInt = { "move" };
        Matcher matcher = Pattern.compile("(.+?)\\((.*)\\);$").matcher(command);
        boolean ifBasic = false;
        if (matcher.find()) {
            for (int i = 0; i < basicCommands.length; i++) {
                if (matcher.group(1).equals(basicCommands[i]) && matcher.group(2).equals("")) {
                    ifBasic = true;
                }
            }
            for (int i = 0; i < basicCommandsWithInt.length; i++) {
                Matcher intMatcher = Pattern.compile("^([0-9]*)$").matcher(matcher.group(2));
                if (matcher.group(1).equals(basicCommandsWithInt[i]) && intMatcher.find()) {
                    ifBasic = true;
                }
            }
        }
        return ifBasic;
    }

    private void runCodeList(List<String> codeList) {
        for (int i = 0; i < codeList.size(); i++) {
            Matcher matcherAction = Pattern.compile("(.+?)\\((.*)\\);$").matcher(codeList.get(i));
            if (matcherAction.find()) {
                action(matcherAction.group(1), matcherAction.group(2));
                if (ifOver) {
                    break;
                }
            }
        }
    }

    public void runCodeInTextArea(String codeText) {
        ifBegin = true;
        String[] codes = codeText.split("\n(\t)*");
        // Input Actions
        int index = 0;
        while (index < codes.length && !ifOver) {
            if (codes.length == 1 && codes[0].equals("")) {
                wrongOutput += "There's no code in the code area.\n";
                break;
            }
            Boolean illegal = true;
            Matcher matcherAction = Pattern.compile("(.+?)\\((.*)\\);$").matcher(codes[index]);
            Matcher matcherFunctionStart = Pattern.compile("(.+?)\\(\\)( )*\\{$").matcher(codes[index]);
            Matcher matcherIfElseStart = Pattern.compile("if( )*\\((.+?)\\(\\)\\)( )*\\{$").matcher(codes[index]);
            // Actions
            if (matcherAction.find()) {
                illegal = false;
                String name = matcherAction.group(1), variable = matcherAction.group(2);
                if (ifBasic(codes[index])) {
                    action(name, variable);
                } else if (name.equals(functionName)) {
                    List<String> codeList = Arrays.asList(commandInFunction);
                    runCodeList(codeList);
                } else {
                    illegal = true;
                }
                if (illegal) {
                    wrongOutput += codes[index] + " is an illegal input.\n";
                }
            }
            // Function Creating
            else if (matcherFunctionStart.find()) {
                int indexFunctionEnd = index + 1;
                List<String> commandInFunctionList = new ArrayList<>();
                String tempFunctionName = matcherFunctionStart.group(1);
                Boolean ifBasic = true, ifNameUsed = false, ifFinish = true;
                if (codes.length - index <= 2) {
                    ifFinish = false;
                    wrongOutput += "This function block should end with \"}\".\n";
                    break;
                }
                if (ifBasic(tempFunctionName + "();")) {
                    ifNameUsed = true;
                    wrongOutput += "The function name can't be any of the basic commands.\n";
                }
                do {
                    commandInFunctionList.add(codes[indexFunctionEnd]);
                    Boolean ifBasicTemp = ifBasic(codes[indexFunctionEnd]);
                    if (!ifBasicTemp) {
                        wrongOutput += codes[indexFunctionEnd] + " in the function " +
                                tempFunctionName + "() is not basic commands.\n";
                    }
                    ifBasic = ifBasic && ifBasicTemp;
                    indexFunctionEnd++;
                    if (codes[indexFunctionEnd].matches("( )*\\}( )*")) {
                        break;
                    } else if (indexFunctionEnd == codes.length - 1) {
                        ifFinish = false;
                        wrongOutput += "This function block should end with \"}\".\n";
                        break;
                    }
                } while (true);
                // Create Function
                if (!ifNameUsed && ifBasic && ifFinish) {
                    functionName = tempFunctionName;
                    commandInFunction = commandInFunctionList.toArray(new String[commandInFunctionList.size()]);
                    logOutput += "You have got " + functionName + "() function.\n";
                }
                index = indexFunctionEnd;
            }
            // If-Else
            else if (matcherIfElseStart.find()) {
                int indexIfElseEnd = index + 1;
                Boolean elseExist = false, ifBasic = true, ifBooleanCode = true, extraElseExist = false,
                        ifFinish = true, condition = true;
                List<String> commandInTrueList = new ArrayList<>(), commandInFalseList = new ArrayList<>();
                if (codes.length - index <= 2) {
                    ifFinish = false;
                    wrongOutput += "This if-else block should end with \"}\".\n";
                    break;
                }
                if (matcherIfElseStart.group(1).equals("noRockPresent")) {
                    condition = !map.checkIfRock();
                } else if (matcherIfElseStart.group(1).equals("noRockInBag")) {
                    condition = map.karel.rockInBag == 0 ? true : false;
                } else {
                    ifBooleanCode = false;
                    wrongOutput += matcherIfElseStart.group(1) + "() is not a boolean function.\n";
                }
                do {
                    if (!elseExist) {
                        commandInTrueList.add(codes[indexIfElseEnd]);
                    } else {
                        commandInFalseList.add(codes[indexIfElseEnd]);
                    }
                    Boolean ifBasicTemp = ifBasic(codes[indexIfElseEnd]);
                    if (!ifBasicTemp) {
                        wrongOutput += codes[indexIfElseEnd] + " in the if-else block is not basic commands.\n";
                    }
                    ifBasic = ifBasic && ifBasicTemp;
                    indexIfElseEnd++;
                    if (codes[indexIfElseEnd].matches("( )*\\}( )*else( )*\\{( )*")) {
                        if (!elseExist) {
                            elseExist = true;
                            indexIfElseEnd++;
                        } else {
                            extraElseExist = true;
                            break;
                        }
                    } else if (codes[indexIfElseEnd].matches("( )*\\}( )*")) {
                        break;
                    } else if (indexIfElseEnd == codes.length - 1) {
                        ifFinish = false;
                        wrongOutput += "This if-else block should end with \"}\".\n";
                        break;
                    }
                } while (true);
                // run
                if (ifBasic && ifBooleanCode && !extraElseExist && ifFinish) {
                    if (condition) {
                        runCodeList(commandInTrueList);
                    } else {
                        runCodeList(commandInFalseList);
                    }
                }
                index = indexIfElseEnd;
            } else {
                wrongOutput += codes[index] + " is an illegal input.\n";
            }
            index++;
        }
    }

    public Save cloneSave() {
        Save newSave = new Save(0);
        newSave.map = this.map.cloneMap();
        if (commandInFunction != null) {
            newSave.commandInFunction = this.commandInFunction.clone();
        }
        newSave.functionName = this.functionName;
        newSave.logOutput = this.logOutput;
        newSave.wrongOutput = this.wrongOutput;
        newSave.codeInTextArea = this.codeInTextArea;
        newSave.ifOver = this.ifOver;
        newSave.ifBegin = this.ifBegin;
        newSave.ifSuccess = this.ifSuccess;
        return newSave;
    }
}