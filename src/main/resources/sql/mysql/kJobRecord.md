selectLastJob
===
    select #use("cols")#
               from k_job_record job where 1=1
               @if(!isEmpty(recordJob)){
               	and job.record_job =  #recordJob#
               @}
               order by job.start_time desc
               limit 1
               
cols
===
    *列名
    job.record_id,
    job.record_job,
    job.start_time,
    job.stop_time,
    job.record_status,
    job.log_file_path,
    job.add_user   
    