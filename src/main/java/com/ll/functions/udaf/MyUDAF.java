package com.ll.functions.udaf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

/**
 * @Author lin_li
 * @Date 2022/1/11 16:20
 *
 * 1. 函数类需要继承 UDAF 类，计算类 Evaluator 实现 UDAFEvaluator 接口
 * 2. 实现函数 init，对UDAF进行初始化
 * 3. 实现函数 iterate，iterate接收传入的参数，并进行内部的轮转。其返回类型为boolean
 * 4. 实现函数 terminatePartial，terminatePartial无参数，其为iterate函数遍历结束后，返回轮转数据，terminatePartial类似于hadoop的Combiner
 * 5. 实现函数 merge，merge接收terminatePartial的返回结果，进行数据merge操作，其返回类型为boolean
 * 6. 实现函数 terminate，terminate返回最终的聚集函数结果
 *
 */
public class MyUDAF extends UDAF {

    public static class AvgState {
        private long mCount;
        private double mSum;
    }

    public static class MyUDAFEvaluator implements UDAFEvaluator {
        AvgState state;

        public MyUDAFEvaluator() {
            super();
            state = new AvgState();
            init();
        }

        /**
         * init函数类似于构造函数，用于UDAF的初始化
         */
        public void init() {
            state.mSum = 0;
            state.mCount = 0;
        }

        /**
         * iterate接收传入的参数，并进行内部的轮转。其返回类型为boolean
         */

        public boolean iterate(Double o) {
            if (o != null) {
                state.mSum += o;
                state.mCount++;
            }
            return true;
        }

        /**
         * terminatePartial无参数，其为iterate函数遍历结束后，返回轮转数据， * terminatePartial类似于hadoop的Combiner * * @return
         */

        public AvgState terminatePartial() {
            // combiner
            return state.mCount == 0 ? null : state;
        }

        /**
         * merge接收terminatePartial的返回结果，进行数据merge操作，其返回类型为boolean
         */

        public boolean merge(AvgState avgState) {
            if (avgState != null) {
                state.mCount += avgState.mCount;
                state.mSum += avgState.mSum;
            }
            return true;
        }

        /**
         * terminate返回最终的聚集函数结果
         */
        public Double terminate() {
            return state.mCount == 0 ? null : Double.valueOf(state.mSum / state.mCount);
        }
    }

}
