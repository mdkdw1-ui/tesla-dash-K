import React, { useState, useEffect, useRef } from 'react';
import { RefreshCw, Battery, Zap, Car, MapPin, ChevronDown } from 'lucide-react';

const cleanAddress = (addr) => {
  if (!addr) return '위치 미상';
  return addr
    .replace(/(서울특별시|경기도|인천광역시|부산광역시|대구광역시|광주광역시|대전광역시|울산광역시|세종특별자치시|제주특별자치도|강원특별자치도|전북특별자치도)/g, '')
    .replace(/(고양시|성남시|수원시|용인시|부천시|안산시|안양시|청주시|천안시|전주시|포항시|창원시|화성시|파주시|김포시|광명시|군포시|이천시|양주시|구리시|안성시|하남시|의왕시|여주시|동두천시|과천시|남양주시)/g, '')
    .replace(/\s+/g, ' ')
    .trim();
};

function KakaoDriveMap({ path = [] }) {
  const mapRef = useRef(null);

  useEffect(() => {
    if (!window.kakao || !window.kakao.maps || !mapRef.current || !path || path.length === 0) return;

    window.kakao.maps.load(() => {
      const linePath = path.map(p => new window.kakao.maps.LatLng(p.latitude, p.longitude));
      const mapOptions = { center: linePath[0], level: 5 };
      const map = new window.kakao.maps.Map(mapRef.current, mapOptions);

      const polyline = new window.kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 4,
        strokeColor: '#38bdf8',
        strokeOpacity: 0.9,
        strokeStyle: 'solid'
      });
      polyline.setMap(map);

      const bounds = new window.kakao.maps.LatLngBounds();
      linePath.forEach(pt => bounds.extend(pt));
      map.setBounds(bounds);
    });
  }, [path]);

  return <div ref={mapRef} className="w-full h-44 rounded-lg overflow-hidden border border-slate-700/80 my-2" />;
}

export default function TeslaDashboard({ initialLogs = [], vehicleData = {} }) {
  const [selectedLog, setSelectedLog] = useState(null);
  const [isSyncing, setIsSyncing] = useState(false);

  const getLogicalDateStr = (dateStr) => {
    if (!dateStr) return '날짜 미상';
    const d = new Date(dateStr);
    d.setHours(d.getHours() - 5);
    return `${d.getMonth() + 1}월 ${d.getDate()}일`;
  };

  const filteredLogs = initialLogs.filter(item => {
    const isDriving = item.move_km > 0 || item.type === 'driving';
    const isCharging = (item.charge_battery && item.charge_battery >= 1) || 
                       (item.use_battery && item.use_battery <= -1) || 
                       item.type === 'charging';
    return isDriving || isCharging;
  });

  const groupedLogs = filteredLogs.reduce((acc, log) => {
    const dateKey = getLogicalDateStr(log.created_at);
    if (!acc[dateKey]) acc[dateKey] = [];
    acc[dateKey].push(log);
    return acc;
  }, {});

  return (
    <div className="min-h-screen bg-[#090a0f] text-slate-100 p-3 font-sans text-xs leading-tight select-none">
      <div className="flex items-center justify-between text-[11px] text-slate-400 mb-2 px-1">
        <span>v1.0.14+14</span>
        <div className="flex items-center gap-1 text-slate-200 font-semibold">
          <span>...SC162401</span>
          <ChevronDown className="w-3 h-3 text-slate-400" />
        </div>
      </div>

      <div className="flex items-center justify-between mb-3 px-1">
        <h1 className="text-lg font-bold text-white tracking-tight">최근 차량 상태</h1>
        <div className="flex items-center gap-2">
          <span className="text-[10px] text-slate-400">18:51 기준</span>
          <button onClick={() => setIsSyncing(true)} className="p-1 rounded-full bg-slate-800/80 text-slate-300">
            <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin text-cyan-400' : ''}`} />
          </button>
        </div>
      </div>

      <div className="bg-[#12131c] border border-purple-500/40 rounded-xl p-2.5 mb-3 flex items-center justify-between">
        <div className="flex items-center gap-1.5 font-bold text-purple-400 text-xs">
          <span className="w-2 h-2 rounded-full bg-purple-500 animate-pulse" />
          현재: 온라인
        </div>
        <div className="flex items-center gap-3 font-mono font-semibold text-slate-200 text-xs">
          <span className="flex items-center gap-1 text-emerald-400"><Battery className="w-3.5 h-3.5" /> 58%</span>
          <span className="flex items-center gap-1 text-slate-300"><MapPin className="w-3.5 h-3.5 text-red-400" /> 6714 km</span>
        </div>
      </div>

      <div className="bg-[#10121a] border border-slate-800 rounded-xl p-3 mb-3">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-1.5 text-slate-300 font-bold">
            <Zap className="w-3.5 h-3.5 text-indigo-400" />
            최근 전비
            <span className="text-[9px] font-normal px-1.5 py-0.2 rounded bg-indigo-950/80 text-indigo-300 border border-indigo-800/50">실축 용량</span>
          </div>
          <span className="text-[10px] text-slate-500">주행 6건</span>
        </div>

        <div className="text-2xl font-black font-mono text-indigo-400 tracking-tight mb-3">
          5.94 <span className="text-xs font-normal text-slate-400">km/kWh</span>
        </div>

        <div className="grid grid-cols-4 gap-1 pt-2 border-t border-slate-800/80 text-center font-mono">
          <div>
            <div className="text-[9px] text-slate-500 mb-0.5">주행거리</div>
            <div className="font-bold text-slate-200">74.0 km</div>
          </div>
          <div>
            <div className="text-[9px] text-slate-500 mb-0.5">사용 배터리</div>
            <div className="font-bold text-slate-200">20.1%</div>
          </div>
          <div>
            <div className="text-[9px] text-slate-500 mb-0.5">배터리당</div>
            <div className="font-bold text-slate-200">3.69 km/%</div>
          </div>
          <div>
            <div className="text-[9px] text-slate-500 mb-0.5">사용 에너지</div>
            <div className="font-bold text-slate-200">12.5 kWh</div>
          </div>
        </div>
      </div>

      <div className="bg-[#10121a] border border-amber-500/40 rounded-xl p-2.5 mb-3 flex items-center gap-3">
        <div className="w-7 h-7 rounded-lg bg-amber-500/20 border border-amber-500/40 flex items-center justify-center font-bold text-amber-400 text-xs">P</div>
        <div>
          <div className="font-bold text-amber-400 text-xs">주차 1시간 26분</div>
          <div className="text-[10px] text-slate-500">7월 25일 17:24 부터</div>
        </div>
      </div>

      <div className="bg-[#10121a] border border-amber-500/30 rounded-xl p-3 mb-4">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-1.5 font-bold text-slate-200 text-xs">
            <Car className="w-3.5 h-3.5 text-amber-400" /> 타이어 공기압
          </div>
          <div className="text-[10px] text-slate-500 font-mono">7월 25일 17:24 | 🌡️ 33.0°C</div>
        </div>

        <div className="grid grid-cols-2 gap-2 mb-2">
          <div className="bg-[#151722] border border-amber-500/40 rounded-lg p-2 text-center">
            <div className="text-[9px] text-slate-400">앞 왼쪽</div>
            <div className="text-sm font-bold font-mono text-amber-400">34.8 <span className="text-[10px] font-normal text-slate-500">psi</span></div>
          </div>
          <div className="bg-[#151722] border border-amber-500/40 rounded-lg p-2 text-center">
            <div className="text-[9px] text-slate-400">앞 오른쪽</div>
            <div className="text-sm font-bold font-mono text-amber-400">35.5 <span className="text-[10px] font-normal text-slate-500">psi</span></div>
          </div>
          <div className="bg-[#151722] border border-amber-500/40 rounded-lg p-2 text-center">
            <div className="text-[9px] text-slate-400">뒤 왼쪽</div>
            <div className="text-sm font-bold font-mono text-amber-400">35.2 <span className="text-[10px] font-normal text-slate-500">psi</span></div>
          </div>
          <div className="bg-[#151722] border border-amber-500/40 rounded-lg p-2 text-center">
            <div className="text-[9px] text-slate-400">뒤 오른쪽</div>
            <div className="text-sm font-bold font-mono text-amber-400">35.5 <span className="text-[10px] font-normal text-slate-500">psi</span></div>
          </div>
        </div>

        <div className="flex justify-center items-center gap-3 text-[9px] text-slate-400">
          <span className="flex items-center gap-1"><span className="w-1.5 h-1.5 rounded-full bg-emerald-500" /> 정상</span>
          <span className="flex items-center gap-1"><span className="w-1.5 h-1.5 rounded-full bg-amber-500" /> 주의</span>
          <span className="flex items-center gap-1"><span className="w-1.5 h-1.5 rounded-full bg-red-500" /> 위험</span>
        </div>
      </div>

      <div className="space-y-3">
        {Object.entries(groupedLogs).map(([dateLabel, logs]) => (
          <div key={dateLabel}>
            <div className="text-xs font-bold text-slate-400 mb-1.5 px-1">{dateLabel}</div>
            <div className="space-y-1.5">
              {logs.map((log, idx) => {
                const isCharging = (log.charge_battery && log.charge_battery >= 1) || (log.use_battery && log.use_battery <= -1);
                const startAddrClean = cleanAddress(log.start_address);
                const endAddrClean = cleanAddress(log.end_address);

                return (
                  <div key={log.id || idx} className="bg-[#10121a] border border-slate-800 rounded-lg p-2.5 hover:border-slate-700 transition">
                    <div className="flex items-center justify-between mb-1">
                      <span className={`px-2 py-0.5 rounded-md font-bold text-[10px] ${
                        isCharging ? 'bg-emerald-600/90 text-white' : 'bg-sky-600/90 text-white'
                      }`}>
                        {isCharging ? '충전' : '주행'}
                      </span>

                      <span className="text-[11px] text-slate-300 font-mono">
                        {log.created_at ? log.created_at.slice(11, 16) : '16:27'} ~ 17:24 ({log.driving_time || 56}분)
                      </span>
                    </div>

                    {!isCharging && (
                      <div 
                        onClick={() => setSelectedLog(selectedLog?.id === log.id ? null : log)}
                        className="flex items-center gap-1 text-slate-200 text-xs font-medium my-1 cursor-pointer whitespace-nowrap break-keep overflow-x-auto no-scrollbar"
                      >
                        <span className="shrink-0 text-slate-100 font-semibold">{startAddrClean || '은평구 신사1동'}</span>
                        <span className="shrink-0 text-slate-500 text-[10px]">➔</span>
                        <span className="shrink-0 text-slate-300">{endAddrClean || '덕양구 원당동'}</span>
                      </div>
                    )}

                    <div className="flex items-center justify-between text-[11px] text-slate-400 font-mono pt-1 border-t border-slate-800/60 mt-1">
                      <span className="flex items-center gap-1">
                        <Battery className="w-3 h-3 text-emerald-400" />
                        {log.battery_level || 59}%
                      </span>
                      <span className={isCharging ? 'text-emerald-400 font-bold' : 'text-red-400 font-bold'}>
                        {isCharging ? `+${log.charge_battery || 10}%` : `▼ ${Math.abs(log.use_battery || 4)}%`}
                      </span>
                      <span className="text-slate-300">📍 {log.move_km || 14.2} km</span>
                    </div>

                    {selectedLog?.id === log.id && log.location_list && (
                      <KakaoDriveMap path={log.location_list} />
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
