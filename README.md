# Java大作业

## 项目的结构以及各个类之间的关系
src     
|---main    
&emsp;&emsp;|---java    
&emsp;&emsp;&emsp;&emsp;|---Creature.java   
&emsp;&emsp;&emsp;&emsp;|---Huluwa.java     
&emsp;&emsp;&emsp;&emsp;|---Grandpa.java    
&emsp;&emsp;&emsp;&emsp;|---Snake.java      
&emsp;&emsp;&emsp;&emsp;|---Monster.java    
&emsp;&emsp;&emsp;&emsp;|---Soldier.java    
&emsp;&emsp;&emsp;&emsp;|---Formation.java      
&emsp;&emsp;&emsp;&emsp;|---Position.java  
&emsp;&emsp;&emsp;&emsp;|---SpacePanel.java      
&emsp;&emsp;&emsp;&emsp;|---Main.java   

其中Creature是所有生物(Huluwa,Grandpa,Snake,Monster,Soldier)的基类。Formation是阵法的定义。Position定义了空间中的一个位置，一个位置主要是用来放置一个生物。SpacePanel是游戏类，该类继承自JPanel，负责对游戏进行绘制以及进行保存。Main是项目的主类，该类继承自JFrame，负责和用户进行交互，主要是响应键盘事件并调用SpacePanel的相应方法来开始和停止游戏。 

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
游戏中每一个生物移动之后都需要对画面进行更新以及将新的游戏状态记录到文件中，并且每有一个生物死亡都需要判断游戏是否结束。但是如何进行更新画面，如何将游戏状态进行保存以及判断游戏是否结束这些工作不应该由Creature这个类及其子类来实现。在这种情况下使用观察者模式就可以很好的解决这个问题。在Creature类的run方法中，只要在该类的状态发生改变的时候通知观察者(在这里是游戏类),然后每一个生物都可以继续进行自己的动作而不用管其他。游戏在观察到某一个生物的状态发生变化的时候就可以对画面进行更新，保存游戏状态至文件中并且在判断出游戏结束时退出游戏了。
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

## 多线程并发的考虑
在游戏中，每一个生物都是一个单独的线程在一个二维空间上的位置之间进行移动，但是每一个位置在任意时刻有且只能有一个生物，因此这就涉及到多线程的并发。为了实现上述要求，我的实现方法如下：每一个生物在将要移动到下一个位置时都要获得该位置的锁，然后移动到该位置上，最后释放该位置的锁。  
因此我的Position类的部分定义如下：
``` java
public class Position{
    private ReentrantLock lock;//每一个位置都持有一个可重入锁来使得该位置在一个时刻只能有一个生物

    public void lock(){this.lock.lock();}

    public void unlock(){this.lock.unlock();}
}
```
在生物进行移动时的临界区部分代码如下：
``` java
public void run(){
    ......
    Position pos = ... // 获得下一时刻要移动到的位置
    pos.lock()//获得该位置的锁，这样若是该位置已经被另一个生物锁住即另一个生物下一时刻也要移动到该位置上，那么此生物就会在该位置上挂起
    if(pos.getHolder() == null){//若是该位置没有生物那么就直接进行移动
        move(pos);
    }else{                      //若是该位置已经有生物那么就与其战斗
        fight(pos.getHolder());
    }
    pos.unlock();//释放该位置的锁
    ......
}
```
使用上述方法就可以保证一个位置在任意时刻都只会有一个生物在其上，也就是保证了所有的生物线程之间是同步的，不会对临界资源随意访问。

## 生物的移动策略
在这次实验中，我设定的每一个生物的行动策略如下：    
首先得到所有没有死亡并且战斗力低于自己的敌方，然后选择距离自己最近的并且朝着该敌人方向可以进行移动(若是一个位置上的有生物并且是死亡的那么该位置就不可以移动上去)的敌人进行移动。如果上述过程没有成功(即1.没有战斗力低于自己的敌人，2.朝着这些敌人的方向上的下一个位置都无法进行移动)那么就随机在周围八个位置中选择一个可以移动的位置进行移动。

## 游戏的保存和回放方法
在保存中为了能够区分某一个位置是否有生物以及不同生物的类型，我使用的方法如下。
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
然后在每次游戏状态发生改变的时候只需要按序遍历地图上的所有位置并依次对该位置使用上述方法进行保存。在读取游戏时，循环读取一个完整地图的数据并刷新画面就可以实现游戏的回放。使用上述方式就可以在游戏过程中保存和读取地图信息并恢复出一个完整的游戏。    
**_另外在项目主目录下的gameData_back文件是一次游戏的记录文件，可以直接使用这个文件回放该次游戏。_**