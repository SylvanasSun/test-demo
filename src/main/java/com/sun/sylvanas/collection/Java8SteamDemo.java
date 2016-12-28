package com.sun.sylvanas.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Java8新增了Stream,IntStream,LongStream,DoubleStream等流式API.
 * 这些API代表多个支持串行和并行聚集操作的元素.
 * API:
 * Stream提供了大量的方法进行聚集操作,这些方法分为中间方法(intermediate)和末端方法(terminal).
 * Intermediate:
 * 中间操作允许流保持打开状态,并允许直接调用后续方法,中间方法的返回值是另外一个流.
 * filter(Predicate predicate):过滤Stream中所有不符合predicate的元素.
 * mapToXxx(ToXxxFunction mapper):使用ToXxxFunction对流中的元素执行一对一的转换,返回的新流中包含了转换生成的所有元素.
 * peek(Consumer action):依次对每个元素执行一些操作,该方法返回的流与原有流包含相同的元素.
 * distinct():用于排序流中所有重复的元素(判断重复的标准是使用equals()比较的)
 * sorted():用于保证流中的元素在后续的访问中处于有序状态.
 * limit(long maxSize):用于保证对该流的后续访问中最大允许访问的元素个数.
 * <p>
 * Terminal:
 * 末端方法是对流的最终操作,当对某个Stream执行末端方法后,该流将会被"消耗",且不再可用.
 * forEach(Consumer action):遍历流中所有元素,对每个元素执行action.
 * toArray():将流中所有元素转换为一个数组.
 * reduce():该方法有三个重载的版本,都用于通过某种操作来合并流中的元素.
 * min():返回流中所有元素的最小值.
 * max():返回流中所有元素的最大值.
 * count():返回流中所有元素的数量.
 * average():返回流中所有元素的平均值.
 * anyMatch(Predicate predicate):判断流中是否至少包含一个元素符合predicate条件.
 * allMatch(Predicate predicate):判断流中是否每个元素都符合predicate条件.
 * noneMatch(Predicate predicate):判断流中是否所有元素都不符合predicate条件.
 * findFirst():返回流中的第一个元素.
 * findAny():返回流中的任意一个元素.
 * <p>
 * Created by sylvanasp on 2016/12/28.
 */
public class Java8SteamDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("World");
        list.add("Java");
        //统计list集合中包含"a"的元素数量
        System.out.println(list.stream()
                .filter(ele -> ele.contains("a"))
                .count());
        //调用Stream的mapToInt函数获取原有的Stream对应的IntStream
        //遍历输出每个元素的length
        list.stream().mapToInt(ele -> ele.length())
                .forEach(System.out::println);
    }
}
