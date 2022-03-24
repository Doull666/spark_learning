# spark_learning
spark学习

## sparkcore
### 1. spark和hadoop的区别
- Hadoop主要解决，海量数据的存储和海量数据的分析计算
- Spark是一种基于内存的快速、通用、可扩展的大数据分析计算引擎

**总结：** spark 可以替换hadoop原生的mr计算引擎，以加快数据的计算速度；但是spark无法替代hadoop进行海量数据的存储
### 2.spark部署模式
- Local模式：在本地部署单个Spark服务
- Standalone模式：Spark自带的任务调度模式。（国内常用）
- YARN模式：Spark使用Hadoop的YARN组件进行资源与任务调度。（国内常用）
- Mesos模式：Spark使用Mesos平台进行资源与任务的调度
### 3. Spark 和Hadoop 的根本差异是多个作业之间的数据通信问题 :
-  Spark 多个作业之间数据通信是基于内存
- 而 Hadoop 是基于磁盘
### 4. local 本地模式
```
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master local[2] \
./examples/jars/spark-examples_2.11-2.1.1.jar \
100
```
参数|说明
----|----
--class|表示要执行程序的主类
--master *|运行模式的制定
--master local[2]|本地模式，指定两个core
spark-examples_2.11-2.1.1.jar|要运行的程序
100|要运行程序的输入参数
### 5.spark-shell
- 用于代码类调试
- Spark context（sc）：sc是SparkCore程序的入口
- Spark session（spark）：spark是SparkSQL程序入口
### 6.Standalone模式
Standalone模式是Spark自带的资源调动引擎，构建一个由Master + Slave构成的Spark集群，Spark运行在集群中。
```
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master spark://hadoop166:7077 \
--executor-memory 2G \
--total-executor-cores 2 \
./examples/jars/spark-examples_2.11-2.1.1.jar \
10
```
参数|说明
----|----
--class|Spark程序中包含主函数的类
--master|Spark程序运行的模式
--executor-memory 1G|指定每个executor可用内存为1G
--total-executor-cores 2|指定所有executor使用的cpu核数为2个
--executor-cores|指定每个executor使用的cpu核数
application-jar|打包好的应用jar，包含依赖
--master spark://masterIP:7077|指定要连接的集群的master
--deploy-mode client/cluster |Driver程序运行在哪种模式上
### 7.  standalone的两种运行模式 
standalone-client和standalone-cluster两种模式，***主要区别在于：Driver程序的运行节点***
- standalone-client：Driver程序运行在客户端，适用于交互、调试，可以立即看到程序的输出结果。
- standalone-cluster：Driver程序运行在由Master启动的Worker节点，适用于生产环境
### 8.yarn模式
- spark运用hadoop的YARN的任务调度去执行的在nodemanager上启动的spark的executor进程去完成计算
- Spark客户端直接连接Yarn，不需要额外构建Spark集群
### 9. yarn 的两种运行模式
Spark有yarn-client和yarn-cluster两种模式，***主要区别在于：Driver程序的运行节点***
- yarn-client：Driver程序运行在客户端，适用于交互、调试，可以立即看到程序的输出结果。
- yarn-cluster：Driver程序运行在由ResourceManager管理的NodeManager上，再启动的APPMaster，适用于生产环境

**总结：** Driver在集群执行就是集群模式，Driver在客户端执行就是客户端模式
### 10.RDD概述
RDD（Resilient Distributed Dataset）叫做弹性分布式数据集，是Spark中最基本的数据抽象。代码中是一个抽象类，它代表一个弹性的、不可变、可分区、里面的元素可并行计算的集合。
### 11.Transformation转换算子
#### value类型
算子|说明
----|----
map()|一次处理一个分区里面的一个元素
mapPartitions()|一次处理一个分区的数据  
mapPartitionsWithIndex()|每个元素跟所在分区号形成一个元组
flatMap()扁平化|将所有数据放入一个集合中返回
glom()|将每一个分区变成一个数组
groupBy()|将相同的key对应的值放入一个迭代器，即相同key的值存放在一个集合中
filter()|根据条件过滤数据
sample()|从大量的数据中采样，分为有放回和不放回
distinct()|对内部元素进行去重
coalesce()|缩减分区数，用于大数据集过滤后，提高小数据集的执行效率
repartition()|内部其实执行的是coalesce操作，参数shuffle的默认值为true
sortBy()|按照数字大小分别实现正序和倒序排序

***map()和mapPartitions()区别***
- map每次处理一条数据
- mapPartitions：每次处理一个分区的数据，这个分区的数据处理完之后，原RDD中分区的数据才能释放，可能导致OOM
- 开发经验：当内存空间较大的时候建议使用mapPartitions()，以提高处理效率

***coalesce和repartition区别***
- coalesce重新分区，可以选择是否进行shuffle过程。由参数shuffle: Boolean = false/true决定
- repartition实际上是调用的coalesce，进行shuffle
- coalesce一般为缩减分区，如果扩大分区，也不会增加分区总数，意义不大。repartition扩大分区执行shuffle，可以达到扩大分区的效果

### 双value类型
算子|说明
----|----
intersection()|对两个RDD求交集
union()|对两个RDD求并集，生成的RDD分区数是源RDD的分区和
subtract()|对两RDD求差集
zip()|将两个RDD组合成Key/Value形式的RDD

***zip拉链注意事项***
- 分区数不同不能拉链
- 每个分区中元素个数不同不能拉链
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
### Sequence 文件SequenceFile文件是Hadoop用来存储二进制形式的key
1. -value对而设计的一种平面文件(Flat File)
    - SequenceFile 存储格式只支持 (key,value) 类型，即只针对 PairRDD
### Object 对象文件
1. 对象文件是将对象序列化后保存的文件，采用Java的序列化机制
    - Object 存储格式是单一 value 类型，即只支持 value 类型的 RDD
### 文件系统类数据读取与保存
1. Spark的整个生态系统与Hadoop是完全兼容的，所以对于Hadoop所支持的文件类型或者数据库类型，Spark也同样支持
## 累加器
1. 累加器：分布式共享只写变量。（Executor和Executor之间不能读数据）
2. 累加器用来把Executor端变量信息聚合到Driver端。在Driver程序中定义的变量，在Executor端的每个task都会得到这个变量的一份新的副本，每个task更新这些副本的值后，传回Driver端进行merge。
### 系统累加器的使用
1. 累加器定义（SparkContext.accumulator(initialValue)方法），`val sum: LongAccumulator = sc.longAccumulator("sum")`
2. 累加器添加数据（累加器.add方法）,`sum.add(count)`
3. 累加器获取数据（累加器.value）,`sum.value`
4. 注意
    - 把累加器当作一个只写变量，只在行动算子执行后读它的值
    - 累加器放在行动算子中执行
5. 自定义累加器
    - 继承AccumulatorV2，设定输入、输出泛型
    - 重新方法
## 广播变量
1. 广播变量：分布式共享只读变量
2. 广播变量用来高效分发较大的对象，向所有工作节点发送一个较大的只读值，以供一个或多个Spark操作使用
3. 使用广播变量步骤
    1. 调用SparkContext.broadcast(广播变量)创建出一个广播对象，任何可序列化的类型都可以这么实现
    2. 通过广播变量.value，访问该对象的值
    3. 变量只会被发到各个节点一次，作为只读值处理（修改这个值不会影响到别的节点）

## spark sql
### 什么是 spark sql
1. Spark SQL是Spark用来处理结构化数据的一个模块，它提供了2个编程抽象：DataFrame 和 DataSet，并且作为分布式SQL查询引擎的作用
2. spark sql 转化成 RDD，然后提交到集群上执行，大大提高了执行效率
### spark sql 特点
1. 易整合
2. 统一的数据访问方式
3. 兼容 Hive
4. 标准的数据连接
### 什么是 DataFrame
1. 在Spark中，DataFrame是一种以RDD为基础的分布式数据集，类似于传统数据库中的二维表格
2. DataFrame 与 RDD 的主要区别在于，前者带有schema元信息，即DataFrame所表示的二维表数据集的每一列都带有名称和类型
### 什么是 DataSet
1. DataSet是分布式数据集合，是DataFrame的一个扩展
    1. 是DataFrame API的一个扩展，是SparkSQL最新的数据抽象
    2. 用户友好的API风格，既具有类型安全检查也具有DataFrame的查询优化特性
    3. 用样例类来对DataSet中定义数据的结构信息，样例类中每个属性的名称直接映射到DataSet中的字段名称
    4. DataSet是强类型的。比如可以有DataSet[Car]，DataSet[Person]
### SparkSession新的起始点
1. 在老的版本中，SparkSQL提供两种SQL查询起始点：一个叫SQLContext，用于Spark自己提供的SQL查询；一个叫HiveContext，用于连接Hive的查询
2. SparkSession是Spark最新的SQL查询起始点，实质上是SQLContext和HiveContext的组合
### DataFrame
1. 在Spark SQL中SparkSession是创建DataFrame和执行SQL的入口，创建DataFrame有三种方式：
    - 通过Spark的数据源进行创建
    - 从一个存在的RDD进行转换
        1. 如果需要RDD与DF或者DS之间操作，那么都需要引入 import spark.implicits._  （spark不是包名，而是sparkSession对象的名称）
    - 从Hive Table进行查询返回
### DataSet
1. DataSet是具有强类型的数据集合，需要提供对应的类型信息

### 三者共性
1. RDD、DataFrame、DataSet 全都是spark平台下的分布式弹性数据集，为处理超大型数据提供便利
2. 三者都有惰性机制，在进行创建、转换，如map方法时，不会立即执行，只有在遇到Action，如foreach时，三者才会开始遍历运算
3. 三者有许多共同的函数，如filter，排序等
4. 在对DataFrame和Dataset进行操作许多操作都需要这个包:import spark.implicits._（在创建好SparkSession对象后尽量直接导入）

### 三者之间相互转换
- 注意：首先导入隐式转换，import spark.implicits._
1. RDD->DF
    - 将 RDD 内部存储的数据用样例类包装：RDD->case class
    - 将 样例类 包装后的 RDD 数据转化为 DF:RDD.toDF
2. DF->RDD 
    - 将 DF 数据转换为RDD:df.rdd
3. RDD->DS
    - 将 RDD 内部存储的数据用样例类包装：RDD->case class
    - 将 样例类 包装后的 RDD 数据转化为 DF:RDD.toDS
4. DS->RDD 
       - 将 DF 数据转换为RDD:ds.rdd
5. DF->DS
    - DS 是 DF 的一个扩展
    - 将 DF 转化为 DS 即由简转难，DS是强类型数据集合，即对DF定义一个类型即为DS：DF.as[Person]=>DS[Person]
6. DS->DF
    - DS 转化为 DF,即由难转易：ds.toDF

### 对于存在 kerberos 认证的大数据集群，连接 hive 时，需要进行 kerberos 认证
1. 导入配置文件置 resources 目录下:core-site.xml、hdfs-site.xml、hive-site.xml、yarn-site.xml、krb5.conf、supergroup.keytab
2. `System.setProperty("java.security.krb5.conf", "src/main/resources/krb5.conf")`
3. `UserGroupInformation.loginUserFromKeytab("supergroup", "src/main/resources/supergroup.keytab")`

### spark 命令行
1. bin/spark-shell(***推荐***)
    1. sc是SparkCore程序的入口
    2. spark是SparkSQL程序入口
2. bin/spark-sql
    1. Spark SQL CLI可以很方便的在本地运行Hive元数据服务以及从命令行执行查询任务