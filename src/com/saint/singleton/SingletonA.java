package com.saint.singleton;

//1.懒汉模式:懒加载，只有第一次调用的时候才会去实例化，虽然线程安全，但是如果instance已经实例化，第二个线程依旧会被挡在外面。
public class SingletonA {
	private static SingletonA instance = null;

	private SingletonA() {
	}

	public static synchronized SingletonA getInstance() {
		if (instance == null) {
			instance = new SingletonA();
		}
		return instance;
	}
}

// 2.饿汉模式:instance声明为static
// final，类第一次加载到内存的时候就会去实例化，但是如果实例创建是依赖于其他参数或者配置文件就无法使用。
class SingletonB {
	private static final SingletonB instance = new SingletonB();

	private SingletonB() {
	}

	public static SingletonB getInstance() {
		return instance;
	}
}

//3.双重校验锁：线程安全，同时解决了懒汉模式中的效率问题；
//这段代码看起来很完美，很可惜，它是有问题。主要在于instance = new Singleton()这句，这并非是一个原子操作，事实上在 JVM 中这句话大概做了下面 3 件事情。
//1.给 instance 分配内存
//2.调用 Singleton 的构造函数来初始化成员变量
//3.将instance对象指向分配的内存空间（执行完这步 instance 就为非 null 了）
//但是在 JVM 的即时编译器中存在指令重排序的优化。也就是说上面的第二步和第三步的顺序是不能保证的，最终的执行顺序可能是 1-2-3 也可能是 1-3-2
//如果是后者，则在 3 执行完毕、2 未执行之前，被线程二抢占了，这时 instance 已经是非 null 了（但却没有初始化），所以线程二会直接返回 instance，然后使用，然后顺理成章地报错
//所以将instance声明为volatile来禁止指令重排序优化，但是在java5以前的版本是有问题单位，不能使用.
//被volatile修饰的变量的值,不会被本地线程缓存,所有对该变量的读写都是直接操作内存,从而确保多个线程能正确处理该变量
class SingletonC {
	private static volatile SingletonC instance = null;

	private SingletonC() {
	}

	public static SingletonC getInstance() {
		if (instance == null) {
			synchronized (SingletonC.class) {
				if (instance == null) {
					instance = new SingletonC();
				}
			}
		}
		return instance;
	}
}

//4.静态内部类：这种写法仍然使用JVM本身机制保证了线程安全问题；由于 SingletonHolder 是私有的，除了 getInstance() 之外没有办法访问它，
//因此它是懒汉式的；同时读取实例的时候不会进行同步，没有性能缺陷；也不依赖 JDK 版本。
class SingletonD {
	private static class SingletonHolder {
		private static final SingletonD INSTANCE = new SingletonD();
	}

	private SingletonD() {
	}

	public static SingletonD getInstance() {
		return SingletonHolder.INSTANCE;
	}
}

// 5:枚举：我们可以通过EasySingleton.INSTANCE来访问实例，这比调用getInstance()方法简单多了。
// 创建枚举默认就是线程安全的，所以不需要担心double checked locking，而且还能防止反序列化导致重新创建新的对象。
enum EasySingleton {
	INSTANCE;
	private EasySingleton(){
	}
}


