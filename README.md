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
## Action行动算子
行动算子是触发了整个作业的执行。因为转换算子都是懒加载，并不会立即执行。
1. reduce()
    - 聚合，最终只返回一条数据
    - 聚集RDD中的所有元素，先聚合分区内数据，再聚合分区间数据
2. collect()
    - 以数组的形式返回一个数据集
3. count()
    - 返回RDD中元素的个数
4. first()
    - 返回RDD中的第一个元素
5. take()
    - 返回一个由RDD的前n个元素组成的数组
    - 原 RDD 中元素顺序是怎样，取得的数组顺序就是怎样
6. takeOrdered()
    - 返回该RDD排序后的前n个元素组成的数组
    - 先对原 RDD 排序，再取前n个元素
7. aggregate()
    - 首先，对每个分区内元素给定一个初始值
    - 然后，分区内 逻辑处理（注意每个分区内逻辑处理都要加上初始值）
    - 最后，分区间 再进行逻辑处理，分区间逻辑处理需要 额外 再加上一次初始值
8. fold()
    - 与 aggregate() 处理规则相同
    - 分区内 分区间 处理逻辑相同
9. countByKey()
    - 统计每个 key 出现的次数
10. save相关算子
    - saveAsTextFile(path)保存成Text文件
        1. 将数据集的元素以textfile的形式保存到HDFS文件系统或者其他支持的文件系统
        2. 对于每个元素，Spark将会调用toString方法，将它装换为文件中的文本
    - saveAsSequenceFile(path) 保存成Sequencefile文件
        1. 将数据集中的元素以Hadoop Sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统
        2. RDD 只能是 (k,v) 类型才可以使用这种存储
        3. 文件内容也是序列化后的数据
    - saveAsObjectFile(path) 序列化成对象保存到文件
        1. 用于将RDD中的元素序列化成对象，存储到文件中
        2. 文件内容是序列化后的，不能直接读出数据
11. foreach()
    - foreach 直接打印出来的数据是无先后顺序的
    - collect().foreach 打印的数据是依据原 RDD 的顺序打印的