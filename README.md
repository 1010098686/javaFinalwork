# Java大作业

## 使用到的面向对象方法和设计模式

### 继承和多态
由于每个生物（葫芦娃，爷爷，蛇精等）都是一个线程并且他们行动的模式都是一样的，因此将这些生物抽象为一个基类Creature，然后这些具体的生物继承自Creature。
``` java
//Creature的类定义以及其中的一些重要方法，该类继承自Observable是因为使用了观察者模式(之后详细介绍)并且实现了Runnable接口
public abstract class Creature extends Observable implements Runnable{
    public abstract int getPower(); //获得该生物战斗力
    public abstract Image getImage(); //获得该生物的图片
    public abstract boolean sameKind(Creature other); //判断和另一个生物是否同类(同类不需要互相战斗)

    @Override
    private void run(){...}//每一个生物的行为模式都是一样的，因此run方法在基类中就可以给出实现
    private void fight(Creature other){...}//和另一个生物进行战斗决定生死
    private void move(Position pos){...}//移动到指定位置
}
```
这样定义了一个基类之后，在游戏的运行过程中就可以用Creature来统一表示所有生物，并且使用统一的接口，这里就体现了多态的好处：使用统一的接口，却可以在运行时根据该对象的具体类型而调用对应类的实现方法而不需要对每一个类都编写一份代码。

### 观察者模式
游戏中每一个生物移动之后都需要对画面进行更新以及将新的游戏状态记录到文件中，并且每有一个生物死亡都需要判断游戏是否结束。但是如何进行更新画面，如何将游戏状态进行保存以及判断游戏是否结束这些工作不应该由Creature这个类及其子类来实现。在这种情况下使用观察者模式就可以很好的解决这个问题。在Creature类的run方法中，只要在该类的状态发生改变的时候通知观察者(在这里是游戏类),然后每一个生物都可以继续进行自己的动作而不用管其他。游戏在观察到某一个生物的状态发生变化的时候就可以对画面进行更新记忆保存游戏状态至文件中并且在判断出游戏结束时退出游戏了。
``` java
//游戏类的部分代码
public class SpacePanel extends JPabel implements Observer{
    @Iverride
    public void update(Observable o, Object arg){
        //生物状态发生改变，根据arg的类型判断应该进行哪种动作
        ......
    }
}
``` 

### 单例
在这个游戏中，葫芦娃和蛇精阵营都需要按照一定的阵法进行排列。在我的实现中，阵法使用了单例模式。使用单例模式的原因是阵法只是几个生物的相对位置关系，任意给定几个生物，若是要排成一个阵法的话只需要按照这个相对位置关系进行排列即可，也就是说这个阵法不需要有多个实例化的对象只需要一个即可，因此我在实现阵法时使用了单例模式。

## 生物的移动策略
在这次实验中，我设定的每一个生物的行动策略如下：    
首先得到所有没有死亡并且战斗力低于自己的敌方，然后选择距离自己最近的并且朝着该敌人方向可以进行移动(若是一个位置上的有生物并且是死亡的那么该位置就不可以移动上去)的敌人进行移动。如果上述过程没有成功(即1.没有战斗力低于自己的敌人，2.朝着这些敌人的方向上的下一个位置都无法进行移动)那么就随机在周围八个位置中选择一个可以移动的位置进行移动。

## 游戏的保存和回放方法
在保存中为了能够区分是否有生物以及不同生物的类型，我使用的方法如下。
定义了一个枚举类型：
``` java
enum HolderType {NOTHING, Huluwa, Grandpa, Snake, Monster, Soldier}
```
其中除了NOTHING之外，其余的每一个值和一个生物的类名相对应。这样可以在保存和读取的时候利用这个值使用RTTI来获得该类型的一个实例，具体来说，在保存方法如下：
``` java
int order = HolderType.valueOf(creature.getClass().getSimpleName()).ordinal();
boolean dead = creature.isDead();
outputStream.writeInt(order);
outputStream.writeBoolean(dead);
```
读取方法如下：
``` java
int order = inputStream.readInt();
boolean dead = inputStream.readBoolean();
String name = HolderType.values()[order].name();
Class c = Class.forName(name);
Creature = (Creature)c.newInstance();
```
使用上述方式就可以在游戏过程中保存和读取地图信息并恢复出一个完整的游戏。