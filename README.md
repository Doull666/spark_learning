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
10. save相关算
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
## RDD 的依赖关系
RDD和它依赖的父RDD（s）的关系有两种不同的类型，即窄依赖（narrow dependency）和宽依赖（wide dependency） 
    - 窄依赖（OneToOneDependency）：以分区为单位，父 RDD 的一个分区数据，只发往子 RDD 的一个分区，则为窄依赖
    - 宽依赖（ShuffleDependency）：以分区为单位，父 RDD 的一个分区数据，会发往子 RDD 的多个分区，则为宽依赖
    - 宽依赖往往伴随着 Shuffle 过程
### Stage任务划分
1. DAG（Directed Acyclic Graph）有向无环图是由点和线组成的拓扑图形，该图形具有方向，不会闭环
2. RDD任务切分中间分为：Application、Job、Stage和Task
    1. Application：初始化一个SparkContext即生成一个Application
    2. Job：一个Action算子就会生成一个Job
    3. Stage：Stage等于宽依赖的个数加1
    4. Task：一个Stage阶段中，最后一个RDD的分区个数就是Task的个数

注意：Application->Job->Stage->Task每一层都是1对n的关系

## RDD持久化
### RDD Cache缓存
- RDD 通过 Cache 或者 Persist 方法将前面的计算结果缓存，默认情况下会把数据以序列化的形式缓存在 JVM 的堆内存中
- 由于 RDD 是懒加载，所以并不是这两个方法被调用时立即缓存，而是触发后面的 action 算子时，该 RDD 将会被缓存在计算节点的内存中，并供后面重用
- cache操作会增加血缘关系，不改变原有的血缘关系
- 将 cache 缓存创建在行动算子之前，避免运算过多
### RDD CheckPoint检查点
1. 检查点是通过将 RDD 中间结果写入磁盘
2. 为什么要做检查点：由于血缘依赖过长会造成容错成本过高，这样就不如在中间阶段做检查点容错，如果检查点之后有节点出现问题，可以从检查点开始重做血缘，减少了开销。
3. 检查点存储路径：Checkpoint的数据通常是存储在HDFS等容错、高可用的文件系统
4. 检查点数据存储格式为：二进制的文件
5. 检查点切断血缘：在Checkpoint的过程中，该RDD的所有依赖于父RDD中的信息将全部被移除。即检查点将会切断之前的血缘关系
6. 检查点触发时间：对RDD进行checkpoint操作并不会马上被执行，必须执行Action操作才能触发。
7. 设置检查点步骤
    - 设置检查点数据存储路径：sc.setCheckpointDir("./checkpoint1")
    - 调用检查点方法：wordToOneRdd.checkpoint()
8. 只增加 checkpoint，没有增加 Cache 缓存打印
    - 检查点会从头至尾再计算一次 RDD ，并将其存入 checkpoint 中
9. 增加 checkpoint，也增加 Cache 缓存打印
    - 检查点会读取 Cache 中的数据，并把数据存至 checkpoint 中
### 缓存和检查点的区别
1. Cache缓存只是将数据保存起来，不切断血缘依赖。Checkpoint 检查点切断血缘依赖。
2. Cache缓存的数据通常存储在磁盘、内存等地方，可靠性低。Checkpoint 的数据通常存储在 HDFS 等容错、高可用的文件系统，可靠性高。
3. 建议对 checkpoint() 的RDD使用Cache缓存，这样 checkpoint 的job只需从Cache缓存中读取数据即可，否则需要再从头计算一次 RDD。
### 检查点存储到 HDFS 集群
注意事项
1. 设置访问HDFS集群的用户名,`System.setProperty("HADOOP_USER_NAME","chenliu")`
2. 需要设置路径.需要提前在HDFS集群上创建/checkpoint路径,`sc.setCheckpointDir("hdfs://hadoop102:9000/checkpoint")`                                     
### 键值对RDD数据分区
1. Spark目前支持 Hash分区 和 Range分区，和用户 自定义分区
2. Hash分区 为当前的默认分区
3. 分区器直接决定了RDD中分区的个数、RDD中每条数据经过Shuffle后进入哪个分区，进而决定了Reduce的个数
    - 只有Key-Value类型的RDD才有分区器，非Key-Value类型的RDD分区的值是
    - 每个RDD的分区ID范围：0~(numPartitions - 1)，决定这个值是属于那个分区的
4. HashPartitioner 分区的原理
    1. 对于给定的 key，计算其 hashCode，并除以分区的个数取余
    2. 如果余数小于 0，则这个 key 所属的分区 ID：`余数+分区的个数`
    3. 如果余数大于或者等于0，则这个 key 所属的分区 ID：`余数`
5. Ranger 分区
    - RangePartitioner 作用
        1. 将一定范围内的数映射到某一个分区内，尽量保证每个分区中数据量均匀
        2. 分区与分区之间是有序的，一个分区中的元素肯定都是比另一个分区中的元素小或者大，但是分区内的元素是不能保证顺序的
        3. 简单说就是，将一定范围内的数映射到某一个分区内
    - 实现过程
        1. 先从整个 RDD 中采用水塘抽样算法，抽取出样本数据排序，计算出每个分区的最大 key 值，形成一个 Array[KEY] 类型的数据变量 rangeBounds
        2. 判断 key 在 rangeBounds 中所处的范围，给出该 key 值在下一个 RDD 中的分区 id 下标
        3. 该分区器要求 RDD 的 Key 类型必须是可以排序的
        4. 即简而言之，RangePartitioner 先根据 key 进行排序，排完序后按照范围划分，即相邻 key 值得数据，最终大概率排在同一个分区
## 数据读取与保存
1. Spark的数据读取及数据保存可以从两个维度来作区分：文件格式以及文件系统
2. 文件格式分为：Text文件、Json文件、Csv文件、Sequence文件以及Object文件
3. 文件系统分为：本地文件系统、HDFS、HBASE以及数据库
### Text 文件
1. 存储至本地文件系统
2. 所存储的文件是可读的
3. 如果是集群路径：hdfs://hadoop102:9000/input/1.txt
### Json 文件
1. 如果JSON文件中每一行就是一个JSON记录，那么可以通过将JSON文件当做文本文件来读取，然后利用相关的JSON库对每一条数据进行JSON解析
2. 使用RDD读取JSON文件处理很复杂，同时SparkSQL集成了很好的处理JSON文件的方式，所以应用中多是采用SparkSQL处理JSON文件
### Sequence 文件
1. SequenceFile文件是Hadoop用来存储二进制形式的key-value对而设计的一种平面文件(Flat File)
    - SequenceFile 存储格式只支持 (key,value) 类型，即只针对 PairRDD
### Object 对象文件
1. 对象文件是将对象序列化后保存的文件，采用Java的序列化机制
    - Object 存储格式是单一 value 类型，即只支持 value 类型的 RDD
### 文件系统类数据读取与保存
1. Spark的整个生态系统与Hadoop是完全兼容的，所以对于Hadoop所支持的文件类型或者数据库类型，Spark也同样支持