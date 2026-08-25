package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 订单拆分消息
 * {"pOrder" :父订单id}
 * 京东订单可能会被多次拆单； 例如：订单1 首先被拆成订单2、订单3；然后订单2有继续被拆成订单4、订单5；最终订单1的子单是订单3、订单4、订单5；每拆一次单我们都会发送一次拆单消息，但父订单号只会传递订单1（原始单），需要通过查询接口获取到最新所有子单，进行相关更新；
 */
@Data
public class JdVOPOrderSplitMsg
{
    private Long pOrder;
}
