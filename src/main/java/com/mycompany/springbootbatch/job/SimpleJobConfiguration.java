package com.mycompany.springbootbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration // Spring Batch의 모든 Job은 @Configuration으로 등록해서 사용
public class SimpleJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory; // 생성자 DI 받음
	private final StepBuilderFactory stepBuilderFactory; // 생성자 DI 받음

	@Bean
	public Job simpleJob() {
		// simpleJob 이란 이름의 Batch Job을 생성
		// Job 내부의 Step들간에 순서 혹은 처리 흐름을 제어
		return jobBuilderFactory.get("simpleJob")
				.start(simpleStep1(null))
				.next(simpleStep2(null))
				.build();
	}

	@Bean
	@JobScope
	//Batch로 실제 처리하고자 하는 기능과 설정을 모두 포함하는 장소
	//JobParameters를 사용하기 위해선 꼭 @StepScope, @JobScope로 Bean을 생성
	public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
		// Tasklet은 Step안에서 단일로 수행될 커스텀한 기능들을 선언할때 사용
		return stepBuilderFactory.get("simpleStep1").tasklet((contribution, chunkContext) -> {
			log.info(">>>>> This is Step1");
			log.info(">>>>> requestDate = {}", requestDate);
			return RepeatStatus.FINISHED;
		}).build();
	}
	
    @Bean
    @JobScope
    /* @JobScope는 Job 실행시점에 Bean이 생성
     * 
     * @JobScope는 Step 선언문에서 사용 가능
     * Job Parameter의 타입으로 사용할 수 있는 것은 Double, Long, Date, String
     */
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step2");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    
    //@StepScope : @StepScope는 Tasklet이나 ItemReader, ItemWriter, ItemProcessor에서 사용
}