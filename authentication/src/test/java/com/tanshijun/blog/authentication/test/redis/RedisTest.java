package com.tanshijun.blog.authentication.test.redis;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.tanshijun.blog.authentication.config.DefaultRedisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanshijun-pc on 2017/10/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void testMap() {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        final byte[] keyByte = keySerializer.serialize("map:test:2");
        final byte[] fieldByte = keySerializer.serialize("name");
        final byte[] valueByte = valueSerializer.serialize("tanshijun");
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.hSet(keyByte, fieldByte, valueByte);
            }
        });
      }

      @Test
      public void testMap1(){
          JSONObject json = new JSONObject();
          json.put("name","tanshijun");
          json.put("age",25);
          redisTemplate.boundHashOps("map:test:2").put("user",json);
      }
       @Test
       public void testMapGet(){
          Object object = redisTemplate.boundHashOps("map:test:2").get("user");
          logger.info("============resultClass:{}============",object.getClass());
           logger.info("============result:{}============",object);
       }

       @Test
       public void testValueGet(){
           redisTemplate.execute(new RedisCallback<Object>() {
               @Override
               public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                   String value = new String(redisConnection.get("bbb".getBytes()));
                   System.out.println("============value="+value+"=================");
                   return new Object();
               }
           });

       }
       @Test
       public void testMulit(){
           redisTemplate.setEnableTransactionSupport(true);
           long result = redisTemplate.execute(new RedisCallback<Long>() {
               @Override
               public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {

                   System.out.println("=========="+redisConnection.isQueueing()+"==================");
                   redisConnection.set("a".getBytes(),"a".getBytes());
                   byte[] key = "post:b".getBytes();

                   int flag = 2;
                       System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                       redisConnection.multi();
                       redisConnection.set("bbb".getBytes(),"bbb1".getBytes());

                       if(flag == 1){
                           throw new RuntimeException("exception");
                       }
                       redisConnection.set("ccc".getBytes(),"ccc".getBytes());
                       redisConnection.exec();
                   return 20l;
               }
           });

           System.out.println("result:"+result);
       }
}
