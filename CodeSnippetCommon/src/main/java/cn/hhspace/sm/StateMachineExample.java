package cn.hhspace.sm;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/2/28 20:06
 * @Descriptions:
 */

// Example usage
public class StateMachineExample {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.pressButton(); // Please insert coin first.
        vm.insertCoin(5); // Coin inserted.
        vm.pressButton(); // Not enough coin.
        vm.insertCoin(10); // Coin inserted.
        vm.pressButton(); // Item sold.
        vm.pressButton(); // Please insert coin first.
    }
}

class VendingMachine {
    private int coin;
    private State currentState;

    public VendingMachine() {
        coin = 0;
        currentState = new NoCoinState();
    }

    public void insertCoin(int amount) {
        coin += amount;
        currentState.insertCoin(this);
    }

    public void pressButton() {
        currentState.pressButton(this);
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public int getCoin() {
        return coin;
    }
}

interface State {
    void insertCoin(VendingMachine vm);
    void pressButton(VendingMachine vm);
}

class NoCoinState implements State {
    @Override
    public void insertCoin(VendingMachine vm) {
        vm.setState(new CoinInsertedState());
        System.out.println("Coin inserted.");
    }

    @Override
    public void pressButton(VendingMachine vm) {
        System.out.println("Please insert coin first.");
    }
}

class CoinInsertedState implements State {
    @Override
    public void insertCoin(VendingMachine vm) {
        vm.setState(new CoinInsertedState());
        System.out.println("Coin already inserted.");
    }

    @Override
    public void pressButton(VendingMachine vm) {
        if (vm.getCoin() >= 10) {
            vm.setState(new SoldState());
            vm.insertCoin(-10);
            System.out.println("Item sold.");
        } else {
            System.out.println("Not enough coin.");
        }
    }
}

class SoldState implements State {
    @Override
    public void insertCoin(VendingMachine vm) {
        vm.setState(new CoinInsertedState());
        System.out.println("Coin inserted.");
    }

    @Override
    public void pressButton(VendingMachine vm) {
        System.out.println("Please wait for the item.");
    }
}

