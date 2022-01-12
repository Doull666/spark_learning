# spark_learning
spark学习


### key-value 类型
1. paritionBy()
    - 将RDD[K,V]中的K按照指定Partitioner重新进行分区
    - 若原有的RDD和新的RDD是一致的话就不进行分区，否则会产生Shuffle过程
2. 自定义分区
    - 继承 org.apache.spark.Partitioner 类
    - 重写 getPartition 方法，定义内部分区逻辑
3. reduceByKey()
    - 将RDD[K,V]中的元素按照相同的K对V进行聚合
    - reduceByKey 处理数据过程
        1. 整个过程大体分为 Map 阶段，Shuffle 阶段，Reduce 阶段
        2. 每一个分区，启动一个 MapTask 任务，来执行分区内操作，即 Combiner 过程
        3. Combiner 操作实际是以分区为单位，对每个分区内相同 key 的数据，进行 value 的预聚合
        4. 接下来进入 Shuffle 阶段，会进行落盘。
        5. Shuffle 阶段主要对不同分区间的相同 key 的数据进行汇总，即把不同分区相同 key 的数据汇总到一个分区
        6. 最后进入 Reduce 阶段，Reduce 阶段将 Shuffle 阶段 汇总后的数据，再进行聚合
        7. 根据最终需要生成的分区数，会产生对应数的 ReduceTask 任务来处理
    - reduceByKey 有 预聚合 的过程，即 Combiner 过程
4. groupByKey()
    - 将相同 key 的数据汇总生成一个 （k,v）,其中 v 为相同 k 的 value 值组成的一个 seq
    - 处理过程
        1. groupByKey 以分区为单位，直接进行 Shuffle
        2. Reduce 阶段将 Shuffle 后的数据，按照 Key 进行汇总
- reduceByKey和groupByKey区别
    - reduceByKey：按照key进行聚合，在shuffle之前有combine（预聚合）操作，返回结果是RDD[k,v]
    - groupByKey：按照key进行分组，直接进行shuffle
    - 在不影响业务逻辑的前提下，优先选用reduceByKey。求和操作不影响业务逻辑，求平均值影响业务逻辑
5. aggregateByKey()
    - 分区内和分区间各给予一种逻辑来处理数据，即 Map 阶段为 分区内，Shuffle 阶段+Reduce 阶段为 分区间处理
    - 处理逻辑：
        1. 首先，会赋予所有分区所有数据一个初始值，即相当于所有分区所有 key 都会多加一条数据 （k,初始值）
        2. 将 初始值 包含在内，进行 分区内 处理
        3. 分区内 的处理是以分区为单位的，一个分区一个 MapTask，分区之间互不影响
        4. 分区内 最终处理好的数据最终进入 Shuffle 阶段，Shuffle 阶段过程中已经涉及到不同分区间数据的交换
        5. Shuffle 阶段将不同分区的相同 key 的数据合并到一起
        6. 最终，Reduce 阶段拿取 Shuffle 阶段合并 key 后的数据，进行 分区间 逻辑聚合
6. foldByKey()
    - aggregateByKey 的简化，即 分区内逻辑 和 分区间逻辑 相同
7. combineByKey()
    - 转换结构后分区内和分区间操作
    - 处理逻辑：
        1. 首先，会转换数据结构，createCombiner（转换数据的结构）
        2. createCombiner 会遍历分区中所有的元素，以分区为单位，会按照设定逻辑，将分区中的 v 进行逻辑转换；若同一分区中有两个相同 key 的数据，则只转换其中一个的 value
        3. 对转换后的数据进行 分区内 处理
        4. 将 分区内 处理后的数据，再 进行 分区间 处理，最后转换成想要的格式
        5. 代码:
        ```
      val combineRDD: RDD[(String, (Int, Int))] = rdd.combineByKey(
                 (_, 1),
                 (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
                 (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)
               )
      ```
      
8. sortByKey()
    - 按照 k 进行排序
    - 默认升序
9. mapValues()
    - 针对于(K,V)形式的类型只对V进行操作
10. join()
    - 将key相同的数据聚合到一个元组
    - 没关联上的数据直接过滤掉
11. cogroup()
    - 在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的RDD
    - rdd1(K,V) 中将 key 相同的数据放入一个迭代器(K,Iterable<V>)，rdd2(K,W) 中将 key 相同的数据放入一个迭代器(K,Iterable<W>)
    - 最后,将 key 相同两个迭代其合并成(K,(Iterable<V>,Iterable<W>))，rdd1 和 rdd2 未关联上的 key，用一个空的迭代器代替