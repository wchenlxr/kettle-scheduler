selectLastTrans
===
    select #use("cols")#
               from k_trans_record tran where 1=1
               @if(!isEmpty(recordTrans)){
               	and tran.record_trans =  #recordTrans#
               @}
               order by job.start_time desc
               limit 1
               
cols
===
    *列名
    tran.record_id,
    tran.record_trans,
    tran.start_time,
    tran.stop_time,
    tran.record_status,
    tran.log_file_path,
    tran.add_user   
    